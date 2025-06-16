package com.marche.marche.controller;

import com.marche.marche.api.APIResponse;
import com.marche.marche.modele.TypeProduction;
import com.marche.marche.services.TypeProductionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/type-production")
@CrossOrigin(origins = "*")
public class TypeProductionController {
    @Autowired
    private TypeProductionService tps;

    // CONTROLLEUR POUR VOIR LA LISTE DES PRODUITS DU VENDEUR
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAll() {
        try {
            List<Object> obj = new ArrayList<>();
            List<TypeProduction> typeProduction = tps.getAll();
            obj.add(typeProduction);
            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}