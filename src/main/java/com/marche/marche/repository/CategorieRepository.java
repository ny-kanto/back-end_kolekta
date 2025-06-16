package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Integer> {
}
