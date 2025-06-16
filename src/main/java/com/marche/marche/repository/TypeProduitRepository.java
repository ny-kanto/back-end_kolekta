package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.TypeProduit;

@Repository
public interface TypeProduitRepository extends JpaRepository<TypeProduit, Integer> {
}
