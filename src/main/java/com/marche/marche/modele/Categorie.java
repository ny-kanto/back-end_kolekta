package com.marche.marche.modele;

import com.marche.marche.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="categorie")
@SequenceGenerator(
    name = "categorie_seq", 
    sequenceName = "categorie_seq", 
    allocationSize = 1
)
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categorie_seq")
    private int id;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "id_type_produit", referencedColumnName = "id")
    private TypeProduit typeProduit;

    public String getNom() {
        return Utils.capitalizeFirstLetter(nom);
    }

    public void setNom(String nom) {
        String cleanNom = nom.trim();
        cleanNom = cleanNom.replaceAll("\\s+", " ");
        this.nom = cleanNom.toLowerCase();
    }

    public Categorie(String nom, TypeProduit typeProduit) {
        this.nom = nom;
        this.typeProduit = typeProduit;
    }
}
