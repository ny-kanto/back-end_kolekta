package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Commande;
import com.marche.marche.modele.CommandeProduit;
import com.marche.marche.modele.Produit;
import com.marche.marche.utils.CommandeProduitId;

@Repository
public interface CommandeProduitRepository extends JpaRepository<CommandeProduit, CommandeProduitId> {
    CommandeProduit getCommandeProduitByCommandeAndProduit(Commande commande, Produit produit);
}
