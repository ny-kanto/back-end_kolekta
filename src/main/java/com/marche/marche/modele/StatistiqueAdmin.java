package com.marche.marche.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatistiqueAdmin {
    private int id;

    private String nom;

    private double totalVentes;
}
