package com.marche.marche.modele;

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
@Table(name = "panier")
@SequenceGenerator(
    name = "panier_seq", 
    sequenceName = "panier_seq",
    allocationSize = 1
)
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "panier_seq")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_personne", referencedColumnName = "id")
    private Personne personne;

    @Transient
    private Personne vendeur;

    @Transient
    private double prixHt;

    @Transient
    private double prixTva;
    
    @Transient
    private double prixTtc;

    public Panier(Personne personne) {
        this.personne = personne;
    }

    public Panier(int id, Personne vendeur, double prixHt, double prixTva, double prixTtc) {
        this.id = id;
        this.vendeur = vendeur;
        this.prixHt = prixHt;
        this.prixTva = prixTva;
        this.prixTtc = prixTtc;
    }
}
