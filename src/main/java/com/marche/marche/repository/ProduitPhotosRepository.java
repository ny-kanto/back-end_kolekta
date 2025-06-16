package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPhotos;
import java.util.List;

@Repository
public interface ProduitPhotosRepository extends JpaRepository<ProduitPhotos, Integer> {
    List<ProduitPhotos> findByProduit(Produit produit);
}
