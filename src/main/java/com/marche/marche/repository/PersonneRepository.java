package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Utilisateur;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Integer> {
    Personne findByUtilisateur(Utilisateur utilisateur);
}
