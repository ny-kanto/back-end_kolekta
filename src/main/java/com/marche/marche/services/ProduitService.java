package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPhotos;
import com.marche.marche.modele.Region;
import com.marche.marche.modele.Unite;
import com.marche.marche.repository.ProduitRepository;
import com.marche.marche.repository.RegionRepository;
import com.marche.marche.repository.UniteRepository;
import com.marche.marche.modele.Categorie;
import com.marche.marche.repository.CategorieRepository;
import com.marche.marche.modele.Personne;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.ProduitPhotosRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class ProduitService {
    @Autowired
    private ProduitRepository pr;

    @Autowired
    private ProduitPhotosRepository ppr;

    @Autowired
    private CategorieRepository cr;

    @Autowired
    private RegionRepository rr;

    @Autowired
    private PersonneRepository per;

    @Autowired
    private UniteRepository ur;

    @Autowired
    private DataSource dataSource;

    public Produit saveProduit(Produit produit) {
        return pr.save(produit);
    }

    public List<Produit> getAllProduits() {
        return pr.findAll();
    }

    public Produit getProduit(int id) {
        return pr.findById(id).get();
    }

    public List<Produit> getAllProduitsByPersonne(Personne personne) {
        return pr.findByPersonne(personne);
    }

    public Produit updateProduit(int idProduit, Produit produit, Personne personne) {
        Produit existingProduit = pr.findById(idProduit)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID : " + idProduit));

        if (!existingProduit.getPersonne().equals(personne)) {
            throw new IllegalArgumentException(
                    "Le produit avec l'ID : " + idProduit + " n'appartient pas à la personne " + personne.getId());
        }

        existingProduit.setNom(produit.getNom());
        existingProduit.setDescription(produit.getDescription());
        existingProduit.setPrix(produit.getPrix());
        existingProduit.setMinCommande(produit.getMinCommande());
        existingProduit.setDelaisLivraison(produit.getDelaisLivraison());
        existingProduit.setMinCommande(produit.getMinCommande());
        existingProduit.setPersonne(produit.getPersonne());
        existingProduit.setUnite(produit.getUnite());
        existingProduit.setCategorie(produit.getCategorie());

        return pr.save(existingProduit);
    }

    public List<Produit> getProduitPerPage(int idPersonne, int noPage, int nbParPage, String column, int sort) {
        List<Produit> produit = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT\r\n" + //
                    "    P.ID               AS ID_PRODUIT,\r\n" + //
                    "    P.NOM              AS NOM_PRODUIT,\r\n" + //
                    "    P.DESCRIPTION,\r\n" + //
                    "    P.PRIX,\r\n" + //
                    "    P.MIN_COMMANDE,\r\n" + //
                    "    P.DELAIS_LIVRAISON,\r\n" + //
                    "    P.DATE_AJOUT,\r\n" + //
                    "    P.LOCALISATION,\r\n" + //
                    "    C.ID               AS ID_CATEGORIE,\r\n" + //
                    "    C.NOM              AS NOM_CATEGORIE,\r\n" + //
                    "    U.ID               AS ID_UNITE,\r\n" + //
                    "    U.NOM              AS NOM_UNITE,\r\n" + //
                    "    R.ID               AS ID_REGION,\r\n" + //
                    "    R.NOM              AS NOM_REGION\r\n" + //
                    "FROM\r\n" + //
                    "    PRODUIT      P\r\n" + //
                    "    JOIN CATEGORIE C\r\n" + //
                    "    ON C.ID = P.ID_CATEGORIE\r\n" + //
                    "    JOIN UNITE U\r\n" + //
                    "    ON U.ID = P.ID_UNITE\r\n" + //
                    "    JOIN TYPE_PRODUIT TP\r\n" + //
                    "    ON TP.ID = C.ID_TYPE_PRODUIT\r\n" + //
                    "    JOIN REGION R\r\n" + //
                    "    ON R.ID = P.ID_REGION\r\n" + //
                    "WHERE\r\n" + //
                    "    ID_PERSONNE = ?\r\n" + //
                    "ORDER BY "
                    + column + " "
                    + ((sort % 2) == 1 ? "ASC" : "DESC")
                    + " LIMIT ? OFFSET ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);
            ps.setInt(2, nbParPage);
            ps.setInt(3, (noPage - 1) * nbParPage);
            ResultSet rs = ps.executeQuery();

            Produit p;
            Categorie cat;
            Unite u;
            Region r;
            Personne personne = per.findById(idPersonne).get();
            while (rs.next()) {
                p = new Produit();
                cat = cr.findById(rs.getInt("id_categorie")).get();
                u = ur.findById(rs.getInt("id_unite")).get();
                r = rr.findById(rs.getInt("id_region")).get();
                p.setId(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom_produit"));
                p.setDescription(rs.getString("description"));
                p.setPrix(rs.getDouble("prix"));
                p.setMinCommande(rs.getDouble("min_commande"));
                p.setDelaisLivraison(rs.getInt("delais_livraison"));
                p.setDateAjout(rs.getTimestamp("date_ajout"));
                p.setLocalisation(rs.getString("localisation"));
                p.setCategorie(cat);
                p.setRegion(r);
                p.setUnite(u);
                p.setPersonne(personne);
                produit.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return produit;
    }

    public String sql(String nom, double prixMin, double prixMax, int idUnite, int idCategorie, int idTypeProduit,
            int noPage, int nbParPage, String column, int sort) {
        String sql = "SELECT\r\n" + //
                "    P.ID               AS ID_PRODUIT,\r\n" + //
                "    P.NOM              AS NOM_PRODUIT,\r\n" + //
                "    P.DESCRIPTION,\r\n" + //
                "    P.PRIX,\r\n" + //
                "    P.MIN_COMMANDE,\r\n" + //
                "    P.DELAIS_LIVRAISON,\r\n" + //
                "    P.DATE_AJOUT,\r\n" + //
                "    P.LOCALISATION,\r\n" + //
                "    C.ID               AS ID_CATEGORIE,\r\n" + //
                "    C.NOM              AS NOM_CATEGORIE,\r\n" + //
                "    U.ID               AS ID_UNITE,\r\n" + //
                "    U.NOM              AS NOM_UNITE,\r\n" + //
                "    R.ID               AS ID_REGION,\r\n" + //
                "    R.NOM              AS NOM_REGION\r\n" + //
                "FROM\r\n" + //
                "    PRODUIT      P\r\n" + //
                "    JOIN CATEGORIE C\r\n" + //
                "    ON C.ID = P.ID_CATEGORIE\r\n" + //
                "    JOIN UNITE U\r\n" + //
                "    ON U.ID = P.ID_UNITE\r\n" + //
                "    JOIN TYPE_PRODUIT TP\r\n" + //
                "    ON TP.ID = C.ID_TYPE_PRODUIT\r\n" + //
                "    JOIN REGION R\r\n" + //
                "    ON R.ID = P.ID_REGION\r\n" + //
                "WHERE\r\n" + //
                "    1 = 1\r\n" + //
                "    AND ID_PERSONNE = ? ";

        if (nom != null && !nom.isEmpty()) {
            sql += "AND LOWER(P.NOM) LIKE ? ";
        }

        if (prixMin > 0.0) {
            sql += "AND PRIX >= ? ";
        }

        if (prixMax > 0.0) {
            sql += "AND PRIX <= ? ";
        }

        if (idUnite > 0) {
            sql += "AND ID_UNITE = ? ";
        }

        if (idCategorie > 0) {
            sql += "AND ID_CATEGORIE = ? ";
        }

        if (idTypeProduit > 0) {
            sql += "AND ID_TYPE_PRODUIT = ? ";
        }

        sql += "ORDER BY " + column + " " + ((sort % 2) == 1 ? "ASC" : "DESC") + " LIMIT ? OFFSET ? ";

        return sql;
    }

    public List<Produit> getProduitFiltre(int idPersonne, String nom, double prixMin, double prixMax, int idUnite,
            int idCategorie, int idTypeProduit, int noPage, int nbParPage, String column, int sort) {
        List<Produit> produit = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = sql(nom, prixMin, prixMax, idUnite, idCategorie, idTypeProduit, noPage, nbParPage, column,
                    sort);

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);
            int parameterIndex = 2;

            System.out.println("sql : " + sql);

            if (nom != null && !nom.isEmpty()) {
                ps.setString(parameterIndex, "%" + nom.toLowerCase() + "%");
                parameterIndex++;
            }

            if (prixMin > 0.0) {
                ps.setDouble(parameterIndex, prixMin);
                parameterIndex++;
            }

            if (prixMax > 0.0) {
                ps.setDouble(parameterIndex, prixMax);
                parameterIndex++;
            }

            if (idUnite > 0) {
                ps.setInt(parameterIndex, idUnite);
                parameterIndex++;
            }

            if (idCategorie > 0) {
                ps.setInt(parameterIndex, idCategorie);
                parameterIndex++;
            }

            if (idTypeProduit > 0) {
                ps.setInt(parameterIndex, idTypeProduit);
                parameterIndex++;
            }

            ps.setInt(parameterIndex, nbParPage);
            parameterIndex++;

            ps.setInt(parameterIndex, (noPage - 1) * nbParPage);
            parameterIndex++;

            ResultSet rs = ps.executeQuery();
            Categorie cat;
            Unite u;
            Region r;
            while (rs.next()) {
                Produit p = new Produit();
                cat = cr.findById(rs.getInt("id_categorie")).get();
                u = ur.findById(rs.getInt("id_unite")).get();
                r = rr.findById(rs.getInt("id_region")).get();
                p.setId(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom_produit"));
                p.setDescription(rs.getString("description"));
                p.setPrix(rs.getDouble("prix"));
                p.setMinCommande(rs.getDouble("min_commande"));
                p.setDateAjout(rs.getTimestamp("date_ajout"));
                p.setDelaisLivraison(rs.getInt("delais_livraison"));
                p.setLocalisation(rs.getString("localisation"));
                p.setCategorie(cat);
                p.setRegion(r);
                p.setUnite(u);
                p.setPersonne(per.findById(idPersonne).get());
                produit.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    public int countProduitFiltre(int idPersonne, String nom, double prixMin, double prixMax, int idUnite,
            int idCategorie, int idTypeProduit) {
        int countProduit = 0;
        try {
            Connection c = dataSource.getConnection();
            // String sql = "SELECT COUNT(p.id) as countProduit FROM produit p JOIN
            // categorie c ON c.id = p.id_categorie WHERE 1 = 1 AND id_personne = ?";
            String sql = "SELECT\r\n" + //
                    "    COUNT(P.ID)               AS COUNTPRODUIT\r\n" + //
                    "FROM\r\n" + //
                    "    PRODUIT      P\r\n" + //
                    "    JOIN CATEGORIE C\r\n" + //
                    "    ON C.ID = P.ID_CATEGORIE\r\n" + //
                    "    JOIN UNITE U\r\n" + //
                    "    ON U.ID = P.ID_UNITE\r\n" + //
                    "    JOIN TYPE_PRODUIT TP\r\n" + //
                    "    ON TP.ID = C.ID_TYPE_PRODUIT\r\n" + //
                    "    JOIN REGION R\r\n" + //
                    "    ON R.ID = P.ID_REGION\r\n" + //
                    "WHERE\r\n" + //
                    "    1 = 1\r\n" + //
                    "    AND ID_PERSONNE = ? ";

            if (nom != null && !nom.isEmpty()) {
                sql += " AND LOWER(P.NOM) LIKE ?";
            }
            if (prixMin > 0.0) {
                sql += " AND P.PRIX >= ?";
            }
            if (prixMax > 0.0) {
                sql += " AND P.PRIX <= ?";
            }
            if (idUnite > 0) {
                sql += " AND P.ID_UNITE = ?";
            }
            if (idCategorie > 0) {
                sql += " AND P.ID_CATEGORIE = ?";
            }
            if (idTypeProduit > 0) {
                sql += " AND C.ID_TYPE_PRODUIT = ?";
            }

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);

            int parameterIndex = 2;
            if (nom != null && !nom.isEmpty()) {
                ps.setString(parameterIndex, "%" + nom.toLowerCase() + "%");
                parameterIndex++;
            }
            if (prixMin > 0.0) {
                ps.setDouble(parameterIndex, prixMin);
                parameterIndex++;
            }
            if (prixMax > 0.0) {
                ps.setDouble(parameterIndex, prixMax);
                parameterIndex++;
            }
            if (idUnite > 0) {
                ps.setInt(parameterIndex, idUnite);
                parameterIndex++;
            }
            if (idCategorie > 0) {
                ps.setInt(parameterIndex, idCategorie);
                parameterIndex++;
            }
            if (idTypeProduit > 0) {
                ps.setInt(parameterIndex, idTypeProduit);
                parameterIndex++;
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                countProduit = rs.getInt("countProduit");
            }

            rs.close();
            ps.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countProduit;
    }

    public int countProduit(Personne p) {
        int countProduit = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(id) as countProduit FROM produit where id_personne = ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                countProduit = rs.getInt("countProduit");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countProduit;
    }

    public int countProduit() {
        int countProduit = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(id) as countProduit FROM produit";

            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                countProduit = rs.getInt("countProduit");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countProduit;
    }

    public void deleteProduit(int idProduit, Personne personne) {
        Optional<Produit> existingProduit = pr.findById(idProduit);
        List<ProduitPhotos> pp = ppr.findByProduit(existingProduit.get());

        for (ProduitPhotos p : pp) {
            ppr.delete(p);
        }

        if (existingProduit.isPresent() && existingProduit.get().getPersonne().equals(personne)) {
            pr.delete(existingProduit.get());
        } else {
            throw new IllegalArgumentException(
                    "Produit de la personne " + personne.getId() + " non trouvé avec l'ID : " + idProduit);
        }
    }

    public List<Produit> getProduitUserPerPage(int noPage, int nbParPage, String column, int sort) {
        List<Produit> produit = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM v_produit_note_stock p ORDER BY " + column + " "
                    + ((sort % 2) == 1 ? "ASC" : "DESC")
                    + " LIMIT ? OFFSET ?";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, nbParPage);
            ps.setInt(2, (noPage - 1) * nbParPage);
            System.out.println("sql : " + ps);
            ResultSet rs = ps.executeQuery();

            Produit p;
            Categorie cat;
            Unite u;
            Personne personne;
            Region r;
            while (rs.next()) {
                p = new Produit();
                cat = cr.findById(rs.getInt("id_categorie")).get();
                personne = per.findById(rs.getInt("id_personne")).get();
                u = ur.findById(rs.getInt("id_unite")).get();
                r = rr.findById(rs.getInt("id_region")).get();
                p.setId(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom_produit"));
                p.setDescription(rs.getString("description_produit"));
                p.setPrix(rs.getDouble("prix_produit"));
                p.setMinCommande(rs.getDouble("min_commande_produit"));
                p.setDateAjout(rs.getTimestamp("date_ajout_produit"));
                p.setDelaisLivraison(rs.getInt("delais_livraison_produit"));
                p.setLocalisation(rs.getString("localisation_produit"));
                p.setCategorie(cat);
                p.setRegion(r);
                p.setUnite(u);
                p.setPersonne(personne);
                p.setAverageRating(rs.getDouble("note_produit"));
                p.setStock(rs.getDouble("reste_stock"));
                produit.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return produit;
    }

    public String sqlUser(String nom, int idLocalisation, int idTypeProduction, int disponibilite, double prixMin,
            double prixMax, int idCategorie, int idTypeProduit, int noPage, int nbParPage, String column, int sort) {
        String sql = "SELECT * FROM v_produit_note_stock p JOIN categorie c ON c.id = p.id_categorie JOIN personne pe ON pe.id = p.id_personne WHERE 1 = 1 ";

        if (nom != null && !nom.isEmpty()) {
            sql += "AND LOWER(p.nom_produit) LIKE ? ";
        }

        if (idLocalisation > 0) {
            sql += "AND p.id_region = ? ";
        }

        if (idTypeProduction > 0) {
            sql += "AND pe.id_type_production = ? ";
        }

        if (prixMin > 0.0) {
            sql += "AND p.prix_produit >= ? ";
        }

        if (prixMax > 0.0) {
            sql += "AND p.prix_produit <= ? ";
        }

        if (idCategorie > 0) {
            sql += "AND p.id_categorie = ? ";
        }

        if (idTypeProduit > 0) {
            sql += "AND c.id_type_produit = ? ";
        }

        if (disponibilite > 0) {
            sql += "AND p.reste_stock > 0 ";
        }

        sql += "ORDER BY " + column + " " + ((sort % 2) == 1 ? "ASC" : "DESC") + " LIMIT ? OFFSET ? ";

        return sql;
    }

    public List<Produit> getProduitUserFiltre(String nom, int idLocalisation, int idTypeProduction, int disponibilite,
            double prixMin, double prixMax, int idCategorie, int idTypeProduit, int noPage, int nbParPage,
            String column, int sort) {
        List<Produit> produit = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = sqlUser(nom, idLocalisation, idTypeProduction, disponibilite, prixMin, prixMax, idCategorie,
                    idTypeProduit, noPage, nbParPage, column, sort);

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            int parameterIndex = 1;

            if (nom != null && !nom.isEmpty()) {
                ps.setString(parameterIndex, "%" + nom.toLowerCase() + "%");
                parameterIndex++;
            }

            if (idLocalisation > 0) {
                ps.setInt(parameterIndex, idLocalisation);
                parameterIndex++;
            }

            if (idTypeProduction > 0) {
                ps.setInt(parameterIndex, idTypeProduction);
                parameterIndex++;
            }

            if (prixMin > 0.0) {
                ps.setDouble(parameterIndex, prixMin);
                parameterIndex++;
            }

            if (prixMax > 0.0) {
                ps.setDouble(parameterIndex, prixMax);
                parameterIndex++;
            }

            if (idCategorie > 0) {
                ps.setInt(parameterIndex, idCategorie);
                parameterIndex++;
            }

            if (idTypeProduit > 0) {
                ps.setInt(parameterIndex, idTypeProduit);
                parameterIndex++;
            }

            ps.setInt(parameterIndex, nbParPage);
            parameterIndex++;

            ps.setInt(parameterIndex, (noPage - 1) * nbParPage);
            parameterIndex++;

            ResultSet rs = ps.executeQuery();

            Categorie cat;
            Unite u;
            Produit p;
            Personne personne;
            Region r;
            while (rs.next()) {
                p = new Produit();
                cat = cr.findById(rs.getInt("id_categorie")).get();
                personne = per.findById(rs.getInt("id_personne")).get();
                u = ur.findById(rs.getInt("id_unite")).get();
                r = rr.findById(rs.getInt("id_region")).get();
                p.setId(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom_produit"));
                p.setDescription(rs.getString("description_produit"));
                p.setPrix(rs.getDouble("prix_produit"));
                p.setMinCommande(rs.getDouble("min_commande_produit"));
                p.setDateAjout(rs.getTimestamp("date_ajout_produit"));
                p.setDelaisLivraison(rs.getInt("delais_livraison_produit"));
                p.setLocalisation(rs.getString("localisation_produit"));
                p.setCategorie(cat);
                p.setRegion(r);
                p.setUnite(u);
                p.setPersonne(personne);
                p.setAverageRating(rs.getDouble("note_produit"));
                p.setStock(rs.getDouble("reste_stock"));
                produit.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    public int countProduitFiltreUser(String nom, int idLocalisation, int idTypeProduction, int disponibilite,
            double prixMin, double prixMax, int idCategorie, int idTypeProduit) {
        int count = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT COUNT(*) FROM v_produit_note_stock p JOIN categorie c ON c.id = p.id_categorie " +
                    "JOIN personne pe ON pe.id = p.id_personne WHERE 1 = 1 ";

            if (nom != null && !nom.isEmpty()) {
                sql += "AND LOWER(p.nom_produit) LIKE ? ";
            }

            if (idLocalisation > 0) {
                sql += "AND p.id_region = ? ";
            }

            if (idTypeProduction > 0) {
                sql += "AND pe.id_type_production = ? ";
            }

            if (prixMin > 0.0) {
                sql += "AND p.prix_produit >= ? ";
            }

            if (prixMax > 0.0) {
                sql += "AND p.prix_produit <= ? ";
            }

            if (idCategorie > 0) {
                sql += "AND p.id_categorie = ? ";
            }

            if (idTypeProduit > 0) {
                sql += "AND c.id_type_produit = ? ";
            }

            if (disponibilite > 0) {
                sql += "AND p.reste_stock > 0 ";
            }

            PreparedStatement ps = c.prepareStatement(sql);
            int parameterIndex = 1;

            if (nom != null && !nom.isEmpty()) {
                ps.setString(parameterIndex, "%" + nom.toLowerCase() + "%");
                parameterIndex++;
            }

            if (idLocalisation > 0) {
                ps.setInt(parameterIndex, idLocalisation);
                parameterIndex++;
            }

            if (idTypeProduction > 0) {
                ps.setInt(parameterIndex, idTypeProduction);
                parameterIndex++;
            }

            if (prixMin > 0.0) {
                ps.setDouble(parameterIndex, prixMin);
                parameterIndex++;
            }

            if (prixMax > 0.0) {
                ps.setDouble(parameterIndex, prixMax);
                parameterIndex++;
            }

            if (idCategorie > 0) {
                ps.setInt(parameterIndex, idCategorie);
                parameterIndex++;
            }

            if (idTypeProduit > 0) {
                ps.setInt(parameterIndex, idTypeProduit);
                parameterIndex++;
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public double produitPrixMin() {
        return pr.produitMinPrice();
    }

    public double produitPrixMax() {
        return pr.produitMaxPrice();
    }

    public List<Produit> getProduitNoteStockByPersonne(Personne personne) {
        List<Produit> produit = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM v_produit_note_stock p WHERE p.id_personne = ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ResultSet rs = ps.executeQuery();

            Produit p;
            Categorie cat;
            Unite u;
            Region r;
            while (rs.next()) {
                p = new Produit();
                cat = cr.findById(rs.getInt("id_categorie")).get();
                u = ur.findById(rs.getInt("id_unite")).get();
                r = rr.findById(rs.getInt("id_region")).get();
                p.setId(rs.getInt("id_produit"));
                p.setNom(rs.getString("nom_produit"));
                p.setDescription(rs.getString("description_produit"));
                p.setPrix(rs.getDouble("prix_produit"));
                p.setMinCommande(rs.getDouble("min_commande_produit"));
                p.setDateAjout(rs.getTimestamp("date_ajout_produit"));
                p.setDelaisLivraison(rs.getInt("delais_livraison_produit"));
                p.setLocalisation(rs.getString("localisation_produit"));
                p.setCategorie(cat);
                p.setRegion(r);
                p.setUnite(u);
                p.setPersonne(personne);
                p.setAverageRating(rs.getDouble("note_produit"));
                p.setStock(rs.getDouble("reste_stock"));
                produit.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return produit;
    }
}
