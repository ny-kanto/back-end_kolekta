package com.marche.marche.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Statistique;
import com.marche.marche.modele.StatistiqueAdmin;

@Service
public class StatistiqueService {
    @Autowired
    private DataSource dataSource;

    public List<Statistique> getStatistiquesByProduitByAnnee(Produit produit, int annee) {
        List<Statistique> statistiques = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "WITH ANNEE_DISTINCTE AS (\n" + //
                    "    SELECT\n" + //
                    "        DISTINCT EXTRACT(YEAR FROM DATE_COMMANDE) AS ANNEE\n" + //
                    "    FROM\n" + //
                    "        COMMANDE\n" + //
                    "), MOIS_ANNEE AS (\n" + //
                    "    SELECT\n" + //
                    "        GENERATE_SERIES(1, 12) AS MOIS,\n" + //
                    "        ANNEE\n" + //
                    "    FROM\n" + //
                    "        ANNEE_DISTINCTE\n" + //
                    ")\n" + //
                    "SELECT\n" + //
                    "    P.ID                                             AS ID_PRODUIT,\n" + //
                    "    P.ID_UNITE                                       AS ID_UNITE,\n" + //
                    "    COALESCE(SUM(CP.QUANTITE), 0)                    AS TOTAL_VENDUS,\n" + //
                    "    COALESCE(SUM(CP.PRIX_UNITAIRE * CP.QUANTITE), 0) AS TOTAL_VENTES,\n" + //
                    "    MA.MOIS                                          AS MOIS,\n" + //
                    "    MA.ANNEE                                         AS ANNEE\n" + //
                    "FROM\n" + //
                    "    MOIS_ANNEE       MA\n" + //
                    "    LEFT JOIN COMMANDE C\n" + //
                    "    ON EXTRACT(MONTH FROM C.DATE_COMMANDE) = MA.MOIS\n" + //
                    "    AND EXTRACT(YEAR FROM C.DATE_COMMANDE) = MA.ANNEE\n" + //
                    "    LEFT JOIN COMMANDE_PRODUIT CP\n" + //
                    "    ON CP.ID_COMMANDE = C.ID\n" + //
                    "    AND CP.STATUS_COMMANDE >= 1\n" + //
                    "    LEFT JOIN PRODUIT P\n" + //
                    "    ON CP.ID_PRODUIT = P.ID\n" + //
                    "WHERE\n" + //
                    "    MA.ANNEE = ?\n" + //
                    "    AND P.ID = ?\n" + //
                    "GROUP BY\n" + //
                    "    P.ID,\n" + //
                    "    P.ID_UNITE,\n" + //
                    "    MA.MOIS,\n" + //
                    "    MA.ANNEE\n" + //
                    "ORDER BY\n" + //
                    "    MA.ANNEE,\n" + //
                    "    MA.MOIS;";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, annee);
            ps.setInt(2, produit.getId());
            ResultSet rs = ps.executeQuery();

            Statistique st;

            while (rs.next()) {
                st = new Statistique(produit.getId(), produit.getNom(), produit.getUnite().getId(),
                        produit.getUnite().getNom(), rs.getDouble("total_vendus"), rs.getDouble("total_ventes"),
                        rs.getInt("mois"), rs.getInt("annee"));
                statistiques.add(st);
            }

            // Compléter les données manquantes pour chaque mois
            for (int mois = 1; mois <= 12; mois++) {
                boolean moisExistant = false;
                for (Statistique stat : statistiques) {
                    if (stat.getMoisi() == mois) {
                        moisExistant = true;
                        break;
                    }
                }
                // Ajouter un mois manquant avec des ventes et quantités à 0
                if (!moisExistant) {
                    st = new Statistique(produit.getId(), produit.getNom(), produit.getUnite().getId(),
                            produit.getUnite().getNom(), 0, 0, mois, annee);
                    statistiques.add(st);
                }
            }

            // Trier la liste par mois pour conserver un ordre chronologique
            statistiques.sort(Comparator.comparingInt(Statistique::getMoisi));

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return statistiques;
    }

    public List<Statistique> getStatistiquesByVendeur(Personne personne, int annee) {
        List<Statistique> statistiques = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "WITH ANNEE_DISTINCTE AS (\r\n" + //
                    "    SELECT\r\n" + //
                    "        DISTINCT EXTRACT(YEAR FROM DATE_COMMANDE) AS ANNEE\r\n" + //
                    "    FROM\r\n" + //
                    "        COMMANDE\r\n" + //
                    "), MOIS_ANNEE AS (\r\n" + //
                    "    SELECT\r\n" + //
                    "        GENERATE_SERIES(1, 12) AS MOIS,\r\n" + //
                    "        ANNEE\r\n" + //
                    "    FROM\r\n" + //
                    "        ANNEE_DISTINCTE\r\n" + //
                    ")\r\n" + //
                    "SELECT\r\n" + //
                    "    COALESCE(SUM(\r\n" + //
                    "        CASE\r\n" + //
                    "            WHEN P.ID_PERSONNE = ? THEN\r\n" + //
                    "                CP.PRIX_UNITAIRE * CP.QUANTITE\r\n" + //
                    "            ELSE\r\n" + //
                    "                0\r\n" + //
                    "        END), 0) AS TOTAL_VENTES,\r\n" + //
                    "    MA.MOIS      AS MOIS,\r\n" + //
                    "    MA.ANNEE     AS ANNEE\r\n" + //
                    "FROM\r\n" + //
                    "    MOIS_ANNEE       MA\r\n" + //
                    "    LEFT JOIN COMMANDE C\r\n" + //
                    "    ON EXTRACT(MONTH FROM C.DATE_COMMANDE) = MA.MOIS\r\n" + //
                    "    AND EXTRACT(YEAR FROM C.DATE_COMMANDE) = MA.ANNEE\r\n" + //
                    "    LEFT JOIN COMMANDE_PRODUIT CP\r\n" + //
                    "    ON CP.ID_COMMANDE = C.ID\r\n" + //
                    "    AND CP.STATUS_COMMANDE >= 1\r\n" + //
                    "    LEFT JOIN PRODUIT P\r\n" + //
                    "    ON CP.ID_PRODUIT = P.ID\r\n" + //
                    "WHERE\r\n" + //
                    "    MA.ANNEE = ?\r\n" + //
                    "GROUP BY\r\n" + //
                    "    MA.MOIS,\r\n" + //
                    "    MA.ANNEE\r\n" + //
                    "ORDER BY\r\n" + //
                    "    MA.ANNEE,\r\n" + //
                    "    MA.MOIS;";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ps.setInt(2, annee);
            ResultSet rs = ps.executeQuery();

            Statistique st;

            while (rs.next()) {
                st = new Statistique(0, "Tous les produits", 0,
                        "", 0, rs.getDouble("total_ventes"),
                        rs.getInt("mois"), rs.getInt("annee"));
                statistiques.add(st);
            }

            // Compléter les données manquantes pour chaque mois
            for (int mois = 1; mois <= 12; mois++) {
                boolean moisExistant = false;
                for (Statistique stat : statistiques) {
                    if (stat.getMoisi() == mois) {
                        moisExistant = true;
                        break;
                    }
                }
                // Ajouter un mois manquant avec des ventes et quantités à 0
                if (!moisExistant) {
                    st = new Statistique(0, "Tous les produits", 0,
                            "", 0, 0, mois, annee);
                    statistiques.add(st);
                }
            }

            // Trier la liste par mois pour conserver un ordre chronologique
            statistiques.sort(Comparator.comparingInt(Statistique::getMoisi));

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return statistiques;
    }

    public List<Integer> getDateCommandeAnnee(Personne personne) {
        List<Integer> dateAnnee = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "WITH ANNEE_DISTINCTE AS (\r\n" + //
                    "    SELECT\r\n" + //
                    "        DISTINCT EXTRACT(YEAR FROM DATE_COMMANDE) AS ANNEE\r\n" + //
                    "    FROM\r\n" + //
                    "        COMMANDE\r\n" + //
                    "), MOIS_ANNEE AS (\r\n" + //
                    "    SELECT\r\n" + //
                    "        GENERATE_SERIES(1, 12) AS MOIS,\r\n" + //
                    "        ANNEE\r\n" + //
                    "    FROM\r\n" + //
                    "        ANNEE_DISTINCTE\r\n" + //
                    ")\r\n" + //
                    "SELECT\r\n" + //
                    "    DISTINCT(MA.ANNEE) AS ANNEE\r\n" + //
                    "FROM\r\n" + //
                    "    MOIS_ANNEE       MA\r\n" + //
                    "    LEFT JOIN COMMANDE C\r\n" + //
                    "    ON EXTRACT(YEAR FROM C.DATE_COMMANDE) = MA.ANNEE\r\n" + //
                    "    LEFT JOIN COMMANDE_PRODUIT CP\r\n" + //
                    "    ON CP.ID_COMMANDE = C.ID\r\n" + //
                    "    LEFT JOIN PRODUIT P\r\n" + //
                    "    ON CP.ID_PRODUIT = P.ID\r\n" + //
                    "WHERE\r\n" + //
                    "    CP.STATUS_COMMANDE >= 1\r\n" + //
                    "    AND P.ID_PERSONNE = ?\r\n" + //
                    "    OR C.ID IS NULL\r\n" + //
                    "GROUP BY\r\n" + //
                    "    MA.ANNEE\r\n" + //
                    "ORDER BY\r\n" + //
                    "    MA.ANNEE;";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ResultSet rs = ps.executeQuery();

            int annee = 0;

            while (rs.next()) {
                annee = rs.getInt("annee");
                dateAnnee.add(annee);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return dateAnnee;
    }

    public double getTotalVentes(Personne personne) {
        double totalVente = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT\r\n" + //
                    "    COALESCE(SUM(CP.PRIX_UNITAIRE * CP.QUANTITE), 0) AS TOTAL_VENTES\r\n" + //
                    "FROM\r\n" + //
                    "    COMMANDE_PRODUIT CP\r\n" + //
                    "    JOIN COMMANDE C\r\n" + //
                    "    ON CP.ID_COMMANDE = C.ID\r\n" + //
                    "    JOIN PRODUIT P\r\n" + //
                    "    ON CP.ID_PRODUIT = P.ID\r\n" + //
                    "WHERE\r\n" + //
                    "    CP.STATUS_COMMANDE >= 1\r\n" + //
                    "    AND P.ID_PERSONNE = ?;";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalVente = rs.getDouble("total_ventes");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return totalVente;
    }

    public List<StatistiqueAdmin> getStatistiqueAdminByCategory() {
        List<StatistiqueAdmin> statistiques = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM TOTAL_VENTES_PAR_CATEGORIE";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StatistiqueAdmin st;

            while (rs.next()) {
                st = new StatistiqueAdmin(rs.getInt("id_categorie"), rs.getString("categorie_nom"),
                        rs.getDouble("total_ventes"));
                statistiques.add(st);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return statistiques;
    }

    public List<StatistiqueAdmin> getStatistiqueAdminByTypeProduit() {
        List<StatistiqueAdmin> statistiques = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM TOTAL_VENTES_PAR_TYPE_PRODUIT";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StatistiqueAdmin st;

            while (rs.next()) {
                st = new StatistiqueAdmin(rs.getInt("id_type_produit"), rs.getString("type_produit_nom"),
                        rs.getDouble("total_ventes"));
                statistiques.add(st);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return statistiques;
    }

    public List<StatistiqueAdmin> getStatistiqueAdminByRegion() {
        List<StatistiqueAdmin> statistiques = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM TOTAL_VENTES_PAR_REGION ORDER BY TOTAL_VENTES DESC";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StatistiqueAdmin st;

            while (rs.next()) {
                st = new StatistiqueAdmin(rs.getInt("id_region"), rs.getString("region_nom"),
                        rs.getDouble("total_ventes"));
                statistiques.add(st);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return statistiques;
    }
}
