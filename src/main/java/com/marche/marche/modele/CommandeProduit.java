package com.marche.marche.modele;

import com.marche.marche.utils.CommandeProduitId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commande_produit")
@IdClass(CommandeProduitId.class)
public class CommandeProduit {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_commande", referencedColumnName = "id")
    private Commande commande;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id")
    private Produit produit;

    private double quantite;

    @Column(name = "prix_unitaire")
    private double prixUnitaire;

    @Column(name = "status_commande")
    private int statusCommande;

    @Transient
    private double total;

    @Transient
    private double tva;

    public CommandeProduit(Commande commande, Produit produit, double quantite, double prixUnitaire,
            int statusCommande) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.statusCommande = statusCommande;
    }

}
