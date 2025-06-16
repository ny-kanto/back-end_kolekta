package com.marche.marche.services;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Panier;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.ProduitPanier;
import com.marche.marche.repository.PanierRepository;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.ProduitPanierRepository;

@Service
public class PanierService {
    @Autowired
    private PanierRepository pr;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProduitPanierRepository ppr;

    @Autowired
    private PersonneRepository per;

    public void savePanier(Panier panier) {
        pr.save(panier);
    }

    public Panier getPanierByPersonne(Personne p) {
        return pr.findByPersonne(p);
    }

    public ProduitPanier saveProduitPanier(ProduitPanier pp) {
        return ppr.save(pp);
    }

    public void deleteProduitPanier(Panier panier) {
        List<ProduitPanier> produits = ppr.findByPanier(panier);
        ppr.deleteAll(produits);
    }

    public List<ProduitPanier> listProduitPanier(Panier panier) {
        return ppr.findByPanierOrderByProduit(panier);
    }

    public int countProduitPanier(Panier panier) {
        return ppr.countByPanier(panier);
    }

    public void deleteProduitPanier(Panier panier, Produit produit) {
        ppr.deleteByPanierAndProduit(panier, produit);
    }

    public void updateProduitPanier(double quantite, Panier panier, Produit produit) {
        ppr.updateByPanierAndProduit(quantite, panier, produit);
    }

    public List<Panier> getListPrixByPanier(int idPanier) {
        List<Panier> paniers = new ArrayList<>();

        try {
            Connection c = dataSource.getConnection();

            String sql = "SELECT\r\n" + //
                    "    PP.ID_PANIER,\r\n" + //
                    "    PE.ID                                                                                     AS ID_VENDEUR,\r\n"
                    + //
                    "    COALESCE(SUM(P.PRIX * PP.QUANTITE), 0)                                                    AS PRIX_HT,\r\n"
                    + //
                    "    (COALESCE(SUM(P.PRIX * PP.QUANTITE), 0) * 0.2)                                            AS PRIX_TVA,\r\n"
                    + //
                    "    (COALESCE(SUM(P.PRIX * PP.QUANTITE), 0) + (COALESCE(SUM(P.PRIX * PP.QUANTITE), 0) * 0.2)) AS PRIX_TTC\r\n"
                    + //
                    "FROM\r\n" + //
                    "    PRODUIT_PANIER PP\r\n" + //
                    "    JOIN PRODUIT P\r\n" + //
                    "    ON P.ID = PP.ID_PRODUIT\r\n" + //
                    "    JOIN PERSONNE PE\r\n" + //
                    "    ON PE.ID = P.ID_PERSONNE\r\n" + //
                    "WHERE\r\n" + //
                    "    PP.ID_PANIER = ?\r\n" + //
                    "GROUP BY\r\n" + //
                    "    PP.ID_PANIER,\r\n" + //
                    "    PE.ID";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPanier);

            ResultSet rs = ps.executeQuery();
            Panier panier;
            while (rs.next()) {
                panier = new Panier(idPanier, per.findById(rs.getInt("id_vendeur")).get(), rs.getDouble("prix_ht"),
                        rs.getDouble("prix_tva"), rs.getDouble("prix_ttc"));
                paniers.add(panier);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paniers;
    }
}
