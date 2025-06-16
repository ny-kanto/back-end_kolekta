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
public class ProduitPanierId implements Serializable {
    private int produit;
    private int panier;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProduitPanierId that = (ProduitPanierId) o;
        return Objects.equals(produit, that.produit) && Objects.equals(panier, that.panier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produit, panier);
    }
}
