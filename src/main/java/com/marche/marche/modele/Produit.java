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
@Table(name = "produit")
@SequenceGenerator(
    name = "produit_seq", 
    sequenceName = "produit_seq", 
    allocationSize = 1
)
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produit_seq")
    private int id;
    
    private String nom;

    private String description;

    private double prix;

    @Column(name = "min_commande")
    private double minCommande;

    @Column(name = "delais_livraison")
    private int delaisLivraison;

    @ManyToOne
    @JoinColumn(name = "id_categorie", referencedColumnName = "id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "id_personne", referencedColumnName = "id")
    private Personne personne;

    @ManyToOne
    @JoinColumn(name = "id_unite", referencedColumnName = "id")
    private Unite unite;

    @Column(name = "date_ajout")
    private Timestamp dateAjout;

    private String localisation;

    @ManyToOne
    @JoinColumn(name = "id_region", referencedColumnName = "id")
    private Region region;

    @Transient
    private double averageRating;

    @Transient
    private double totalCount;

    @Transient
    private boolean isNew;

    @Transient
    private double stock;

    public Produit(String nom, String description, double prix, double minCommande, int delaisLivraison,
            Categorie categorie, Personne personne, Unite unite, Timestamp dateAjout, String localisation, Region region) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.minCommande = minCommande;
        this.delaisLivraison = delaisLivraison;
        this.categorie = categorie;
        this.personne = personne;
        this.unite = unite;
        this.dateAjout = dateAjout;
        this.localisation = localisation;
        this.region = region;
    }
}
