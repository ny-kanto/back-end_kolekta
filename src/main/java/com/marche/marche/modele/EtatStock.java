package com.marche.marche.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtatStock {
    private int idProduit;

    private String nomProduit;

    private String nomUnite;

    private double sommeEntree;

    private double sommeSortie;

    private double sommeReserve;

    private double reste;
}
