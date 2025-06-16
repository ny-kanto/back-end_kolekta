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
public class EvaluationId implements Serializable {

    private int personne;
    private int produit;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EvaluationId that = (EvaluationId) o;
        return Objects.equals(personne, that.personne) &&
                Objects.equals(produit, that.produit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personne, produit);
    }
}
