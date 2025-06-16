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
import com.marche.marche.modele.Categorie;
import com.marche.marche.modele.TypeProduit;
import com.marche.marche.modele.Unite;
import com.marche.marche.services.CategorieService;
import com.marche.marche.services.TypeProduitService;

@RestController
@RequestMapping("/categorie")
@CrossOrigin(origins = "*")
public class CategorieController {
    @Autowired
    private CategorieService cs;

    @Autowired
    private TypeProduitService tps;

    // CONTROLLEUR D'AJOUT DE CATEGORIE DU VENDEUR
    @PostMapping("/save")
    public ResponseEntity<APIResponse> saveCategorie(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam String nom, @RequestParam(name = "id_type_produit") int idTypeProduit) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            TypeProduit tp = tps.getTypeProduitById(idTypeProduit);

            Categorie categorie = new Categorie(nom, tp);
            cs.saveCategorie(categorie);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR DE LISTE DE CATEGORIE DU VENDEUR
    @GetMapping("/list")
    public ResponseEntity<APIResponse> listCategorie(@RequestHeader(name = "Authorization") String authorizationHeader) {
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

            List<Categorie> categories = cs.getAll();
            List<TypeProduit> typeProduits = tps.getAll();

            obj.add(categories);
            obj.add(typeProduits);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR DE LISTE DE CATEGORIE DU VENDEUR
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateCategorie(@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id") int id, @RequestParam String nom, @RequestParam(name = "id_type_produit") int idTypeProduit) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            TypeProduit tp = tps.getTypeProduitById(idTypeProduit);

            Categorie categorie = cs.getCategorieById(id);
            categorie.setNom(nom);
            categorie.setTypeProduit(tp);

            cs.saveCategorie(categorie);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
