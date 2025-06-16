package com.marche.marche.configuration;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.marche.marche.authentification.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        if (token != null) {
            // Récupérer la date d'expiration du token
            Date tokenExpirationDate = jwtUtil.parseJwtClaims(token).getExpiration();

            // Ajouter le token à la liste noire
            jwtUtil.addTokenToBlacklist(token, tokenExpirationDate);

            // Nettoyage des tokens expirés après ajout du nouveau token
            jwtUtil.removeExpiredTokens();

            System.out.println("Token ajouté à la liste noire : " + token);
        }
    }
}
