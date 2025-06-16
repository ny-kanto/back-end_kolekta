package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Unite;

@Repository
public interface UniteRepository extends JpaRepository<Unite, Integer> {
}
