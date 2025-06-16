package com.marche.marche.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marche.marche.api.APIResponse;
import com.marche.marche.modele.Unite;
import com.marche.marche.services.UniteService;

@RestController
@RequestMapping("/unite")
@CrossOrigin(origins = "*")
public class UniteController {
    @Autowired
    private UniteService uns;

    // CONTROLLEUR D'AJOUT D'UNITE DU VENDEUR
    @PostMapping("/save")
    public ResponseEntity<APIResponse> saveUnite(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam String nom) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            Unite unite = new Unite(nom);

            uns.saveUnite(unite);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR DE LISTE D'UNITE DU VENDEUR
    @GetMapping("/list")
    public ResponseEntity<APIResponse> listUnite(@RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            List<Object> obj = new ArrayList<>();
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            List<Unite> unites = uns.getAll();
            obj.add(unites);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR DE LISTE D'UNITE DU VENDEUR
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateUnite(@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id") int id, @RequestParam String nom) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            Unite unite = uns.getUniteById(id);
            unite.setNom(nom);

            uns.saveUnite(unite);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
