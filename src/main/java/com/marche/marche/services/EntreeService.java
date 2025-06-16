package com.marche.marche.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Entree;
import com.marche.marche.modele.EtatStock;
import com.marche.marche.modele.Personne;
import com.marche.marche.repository.EntreeRepository;

@Service
public class EntreeService {
    @Autowired
    private EntreeRepository er;

    @Autowired
    private DataSource dataSource;

    public void saveEntree(Entree entree) {
        er.save(entree);
    }

    public List<EtatStock> getEtatStock(int idPersonne, int noPage, int nbParPage) {
        List<EtatStock> etatStock = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM v_etat_stock WHERE id_personne = ? ORDER BY id_produit ASC LIMIT ? OFFSET ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);
            ps.setInt(2, nbParPage);
            ps.setInt(3, (noPage - 1) * nbParPage);
            ResultSet rs = ps.executeQuery();

            EtatStock es;
            while (rs.next()) {
                es = new EtatStock();
                es.setIdProduit(rs.getInt("id_produit"));
                es.setNomProduit(rs.getString("nom_produit"));
                es.setNomUnite(rs.getString("nom_unite"));
                es.setSommeEntree(rs.getDouble("somme_entree"));
                es.setSommeSortie(rs.getDouble("somme_sortie"));
                es.setSommeReserve(rs.getDouble("somme_reserve"));
                es.setReste(rs.getDouble("reste"));
                etatStock.add(es);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etatStock;
    }

    public EtatStock getEtatStockByIdProduit(int idProduit) {
        EtatStock etatStock = new EtatStock();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM v_etat_stock WHERE id_produit = ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idProduit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                etatStock.setIdProduit(rs.getInt("id_produit"));
                etatStock.setNomProduit(rs.getString("nom_produit"));
                etatStock.setNomUnite(rs.getString("nom_unite"));
                etatStock.setSommeEntree(rs.getDouble("somme_entree"));
                etatStock.setSommeSortie(rs.getDouble("somme_sortie"));
                etatStock.setSommeReserve(rs.getDouble("somme_reserve"));
                etatStock.setReste(rs.getDouble("reste"));
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etatStock;
    }

}
