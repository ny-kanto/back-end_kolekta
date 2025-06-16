package com.marche.marche.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Evaluation;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.utils.EvaluationId;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, EvaluationId> {
    List<Evaluation> findByProduit(Produit produit);

    Evaluation findByProduitAndPersonne(Produit produit, Personne personne);

    @Query("SELECT e.note, COUNT(e) FROM Evaluation e WHERE e.produit = :produit AND e.note != 0 GROUP BY e.note")
    List<Object[]> countEvaluationsByNote(@Param("produit") Produit produit);
}
