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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sortie")
@SequenceGenerator(
    name = "sortie_seq", 
    sequenceName = "sortie_seq", 
    allocationSize = 1
)
public class Sortie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sortie_seq")
    private int id;

    @Column(name = "quantite")
    private double quantite;

    @Column(name = "date_sortie")
    private Timestamp dateSortie;

    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id")
    private Produit produit;
    
    public Sortie(double quantite, Timestamp dateSortie, Produit produit) {
        this.quantite = quantite;
        this.dateSortie = dateSortie;
        this.produit = produit;
    }
}
