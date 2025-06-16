package com.marche.marche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.EvaluationService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.UtilisateurService;

import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/evaluation")
@CrossOrigin(origins = "*")
public class EvaluationController {
    @Autowired
    private UtilisateurService us;

    @Autowired
    private PersonneService pes;

    @Autowired
    private ProduitService ps;

    @Autowired
    private EvaluationService evs;

    @Autowired
    private JwtUtil jwtUtil;

    // CONTROLLEUR D'AJOUT NOTE DE PRODUIT PAR L'ACHETEUR
    @PutMapping("/save/{id_produit}")
    public ResponseEntity<APIResponse> saveProduitEvaluation(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_produit") int idProduit, @RequestParam int note) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            List<Object> obj = new ArrayList<>();

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Produit produit = ps.getProduit(idProduit);

            evs.updateEvaluation(produit, p, note);

            obj.add(produit);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
