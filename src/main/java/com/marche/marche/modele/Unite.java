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
@Table(name="unite")
@SequenceGenerator(
    name = "unite_seq", 
    sequenceName = "unite_seq", 
    allocationSize = 1
)
public class Unite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unite_seq")
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

    public Unite(String nom) {
        this.nom = nom;
    }
}
