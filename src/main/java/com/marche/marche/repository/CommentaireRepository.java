package com.marche.marche.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Commentaire;
import com.marche.marche.modele.Produit;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {
    List<Commentaire> findByProduit(Produit produit);
}
