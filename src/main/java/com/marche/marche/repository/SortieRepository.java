package com.marche.marche.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Sortie;

@Repository
public interface SortieRepository extends JpaRepository<Sortie, Integer> {

    Sortie findByProduitAndQuantite(Produit produit, double quantite);

    Sortie findByProduitAndQuantiteAndDateSortie(Produit produit, double quantite, Timestamp dateSortie);
}
