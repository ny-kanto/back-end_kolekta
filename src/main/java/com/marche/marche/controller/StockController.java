package com.marche.marche.controller;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Entree;
import com.marche.marche.modele.EtatStock;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.EntreeService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.UtilisateurService;
import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "*")
public class StockController {
    @Autowired
    private ProduitService ps;

    @Autowired
    private PersonneService pes;

    @Autowired
    private EntreeService es;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private JwtUtil jwtUtil;

    // CONTROLLEUR D'AJOUT DE PRODUIT DU VENDEUR
    @PostMapping("/entree")
    public ResponseEntity<APIResponse> entreeProduct(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(name = "id_produit") String idProduit, @RequestParam String quantite,
            @RequestParam(name = "date_entree") Date dateEntree) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            // {
            // String token = authorizationHeader.substring(7);
            // Claims claims = jwtUtil.parseJwtClaims(token);
            // idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);
            Produit produit = ps.getProduit(Integer.valueOf(idProduit));

            Entree entree = new Entree(Double.valueOf(quantite), dateEntree, produit);

            es.saveEntree(entree);

            List<Object> obj = new ArrayList<>();
            obj.add(entree);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR D'AJOUT DE PRODUIT DU VENDEUR
    @GetMapping("/etat")
    public ResponseEntity<APIResponse> etatStock(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "15") int nbrParPage,
            @RequestParam(defaultValue = "1") int noPage) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);

            List<EtatStock> etatStock = es.getEtatStock(p.getId(), noPage, nbrParPage);

            int totalPages = (int) Math.ceil((double) ps.countProduit(p) / nbrParPage);

            List<Object> obj = new ArrayList<>();
            obj.add(etatStock);
            obj.add(totalPages);
            obj.add(noPage);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}