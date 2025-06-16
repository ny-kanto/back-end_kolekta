package com.marche.marche.controller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.modele.request.LoginReq;
import com.marche.marche.modele.response.ErrorRes;
import com.marche.marche.modele.response.LoginRes;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.UtilisateurRepository;

@RestController
@RequestMapping("/rest/auth")
@CrossOrigin(origins = "*")
public class LoginController {
    private final AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private final UtilisateurRepository ur;
    private final PersonneRepository pr;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UtilisateurRepository ur,
            PersonneRepository pr) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.ur = ur;
        this.pr = pr;
    }

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginReq loginReq) {
        try {
            // System.out.println("User email : " + loginReq.getEmail());
            // System.out.println("User mdp : " + loginReq.getPassword());
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(),
                            loginReq.getPassword()));
            String email = authentication.getName();
            Utilisateur user = ur.findByEmail(email);
            user.setAdmin(((List<? extends GrantedAuthority>) authentication.getAuthorities()).get(0).getAuthority());
            String token = jwtUtil.createToken(user);
            LoginRes loginRes = new LoginRes(email, token, user.getAdmin(pr));

            return ResponseEntity.ok(loginRes);
        } catch (BadCredentialsException e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Email ou mot de passe invalide");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
