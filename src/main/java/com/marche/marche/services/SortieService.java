package com.marche.marche.services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Sortie;
import com.marche.marche.repository.SortieRepository;

@Service
public class SortieService {
    @Autowired
    private SortieRepository sr;

    public void saveSortie(Sortie sortie) {
        sr.save(sortie);
    }

    public Sortie getSortieByProduitAndQuantite(Produit produit, double quantite) {
        return sr.findByProduitAndQuantite(produit, quantite);
    }

    public void deleteSortie(Sortie sortie) {
        sr.delete(sortie);
    }

    public Sortie getSortieByProduitAndQuantiteAndDateSortie(Produit produit, double quantite, Timestamp dateSortie) {
        return sr.findByProduitAndQuantiteAndDateSortie(produit, quantite, dateSortie);
    }
}
