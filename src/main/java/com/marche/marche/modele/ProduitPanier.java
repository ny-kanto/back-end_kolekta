package com.marche.marche.modele;

import com.marche.marche.utils.ProduitPanierId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "produit_panier")
@IdClass(ProduitPanierId.class)
public class ProduitPanier {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id")
    private Produit produit;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_panier", referencedColumnName = "id")
    private Panier panier;

    private double quantite;
}
