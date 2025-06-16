package com.marche.marche.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Commande;
import com.marche.marche.modele.Personne;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    Commande findByDateCommandeAndMontantTotalAndAdresseLivraisonAndNumClientAndPersonne(Timestamp dateCommande,
            double montantTotal, String adresseLivraison, String numClient, Personne personne);
}
