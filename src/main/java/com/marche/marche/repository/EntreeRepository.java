package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Entree;

@Repository
public interface EntreeRepository extends JpaRepository<Entree, Integer> {
}
