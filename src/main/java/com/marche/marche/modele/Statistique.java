package com.marche.marche.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Statistique {
    private int idProduit;

    private String nomProduit;

    private int idUnite;

    private String nomUnite;

    private double totalVendus;

    private double totalVentes;

    private int mois;

    private int annee;

    public String getMois() {
        if (mois == 1) {
            return "Janvier";
        } else if (mois == 2) {
            return "Février";
        } else if (mois == 3) {
            return "Mars";
        } else if (mois == 4) {
            return "Avril";
        } else if (mois == 5) {
            return "Mai";
        } else if (mois == 6) {
            return "Juin";
        } else if (mois == 7) {
            return "Juillet";
        } else if (mois == 8) {
            return "Août";
        } else if (mois == 9) {
            return "Septembre";
        } else if (mois == 10) {
            return "Octobre";
        } else if (mois == 11) {
            return "Novembre";
        } else if (mois == 12) {
            return "Décembre";
        }
        return "";
    }

    public int getMoisi() {
        return this.mois;
    }
}
