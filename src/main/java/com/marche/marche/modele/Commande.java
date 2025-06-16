package com.marche.marche.modele;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "commande")
@SequenceGenerator(
    name = "commande_seq", 
    sequenceName = "commande_seq", 
    allocationSize = 1
)
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commande_seq")
    private int id;

    @Column(name = "date_commande")
    private Timestamp dateCommande;

    @Column(name = "montant_total")
    private double montantTotal;

    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    @Column(name = "num_client")
    private String numClient;

    @ManyToOne
    @JoinColumn(name = "id_personne", referencedColumnName = "id")
    private Personne personne;

    @Transient
    private int messageNonLus;

    @Transient
    private double montantTva;

    @Transient
    private double montantTtc;

    @Column(name = "methode_paiement")
    private int methodePaiement;

    private String reference;

    private int status;
    
    public Commande(Timestamp dateCommande, double montantTotal, String adresseLivraison, String numClient,
            Personne personne) {
        this.dateCommande = dateCommande;
        this.montantTotal = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.numClient = numClient;
        this.personne = personne;
    }

    public Commande(int id, Timestamp dateCommande, double montantTotal, String adresseLivraison, String numClient,
            Personne personne) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.montantTotal = montantTotal;
        this.adresseLivraison = adresseLivraison;
        this.numClient = numClient;
        this.personne = personne;
    }
}
