package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    List<Produit> findByPersonne(Personne personne);

    @Query("SELECT MIN(prix) + (MIN(prix) * 30)/100 FROM Produit")
    double produitMinPrice();

    @Query("SELECT MAX(prix) - (MAX(prix) * 30)/100 FROM Produit")
    double produitMaxPrice();
}
