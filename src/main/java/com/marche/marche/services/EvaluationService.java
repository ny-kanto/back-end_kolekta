package com.marche.marche.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Evaluation;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.repository.EvaluationRepository;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository er;

    public void saveEvaluation(Evaluation evaluation) {
        er.save(evaluation);
    }

    public void updateEvaluation(Produit produit, Personne personne, int note) {
        Evaluation evaluation = er.findByProduitAndPersonne(produit, personne);
        evaluation.setNote(note);
        er.save(evaluation);
    }

    public List<Evaluation> getEvaluationByProduit(Produit produit) {
        return er.findByProduit(produit);
    }

    public Evaluation getEvaluationByProduitAndPersonne(Produit produit, Personne personne) {
        return er.findByProduitAndPersonne(produit, personne);
    }

    public Map<Integer, Long> getEvaluationCountsByNote(Produit produit) {
        List<Object[]> results = er.countEvaluationsByNote(produit);
        Map<Integer, Long> noteCounts = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            noteCounts.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer note = (Integer) result[0];
            Long count = (Long) result[1];
            noteCounts.put(note, count);
        }

        return noteCounts;
    }

}
