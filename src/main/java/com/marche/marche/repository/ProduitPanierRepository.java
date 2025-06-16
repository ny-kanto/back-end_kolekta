package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.marche.marche.modele.Panier;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPanier;
import com.marche.marche.utils.ProduitPanierId;
import java.util.List;

@Repository
public interface ProduitPanierRepository extends JpaRepository<ProduitPanier, ProduitPanierId> {
    void deleteByPanier(Panier panier);

    List<ProduitPanier> findByPanierOrderByProduit(Panier panier);

    @Query("SELECT COUNT(pp) FROM ProduitPanier pp WHERE pp.panier = :panier")
    int countByPanier(@Param("panier") Panier panier);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProduitPanier pp WHERE pp.panier = :panier AND pp.produit = :produit")
    void deleteByPanierAndProduit(@Param("panier") Panier panier, @Param("produit") Produit produit);

    @Modifying
    @Transactional
    @Query("UPDATE ProduitPanier pp SET pp.quantite = :quantite WHERE pp.panier = :panier AND pp.produit = :produit")
    void updateByPanierAndProduit(@Param("quantite") double quantite, @Param("panier") Panier panier,
            @Param("produit") Produit produit);

    List<ProduitPanier> findByPanier(Panier panier);
}
