package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.TypeProduction;

@Repository
public interface TypeProductionRepository extends JpaRepository<TypeProduction, Integer> {
}
