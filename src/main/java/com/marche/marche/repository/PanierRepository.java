package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Panier;
import com.marche.marche.modele.Personne;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Integer> {
    Panier findByPersonne(Personne personne);
}
