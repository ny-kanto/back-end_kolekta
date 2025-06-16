package com.marche.marche.modele;

import com.marche.marche.utils.Utils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="type_produit")
@SequenceGenerator(
    name = "type_produit_seq", 
    sequenceName = "type_produit_seq", 
    allocationSize = 1
)
public class TypeProduit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_produit_seq")
    private int id;

    private String nom;

    public String getNom() {
        return Utils.capitalizeFirstLetter(nom);
    }

    public void setNom(String nom) {
        String cleanNom = nom.trim();
        cleanNom = cleanNom.replaceAll("\\s+", " ");
        this.nom = cleanNom.toLowerCase();
    }
}
