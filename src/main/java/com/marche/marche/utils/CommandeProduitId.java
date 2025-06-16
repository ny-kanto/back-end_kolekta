package com.marche.marche.utils;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeProduitId implements Serializable {

    private int commande;
    private int produit;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CommandeProduitId that = (CommandeProduitId) o;
        return Objects.equals(commande, that.commande) && Objects.equals(produit, that.produit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commande, produit);
    }
}
