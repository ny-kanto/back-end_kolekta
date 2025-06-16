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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="region")
@SequenceGenerator(
    name = "region_seq", 
    sequenceName = "region_seq", 
    allocationSize = 1
)
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_seq")
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
