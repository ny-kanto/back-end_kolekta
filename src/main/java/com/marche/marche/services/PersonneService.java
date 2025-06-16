package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Role;
import com.marche.marche.modele.TypeProduction;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.RoleRepository;
import com.marche.marche.repository.TypeProductionRepository;
import com.marche.marche.repository.UtilisateurRepository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class PersonneService {
    @Autowired
    private PersonneRepository pr;

    @Autowired
    private RoleRepository rr;

    @Autowired
    private TypeProductionRepository tpr;

    @Autowired
    private UtilisateurRepository ur;

    @Autowired
    private DataSource dataSource;

    public void savePersonne(Personne personne) {
        pr.save(personne);
    }

    public Personne getPersonneById(int idPersonne) {
        return pr.findById(idPersonne).get();
    }

    public Personne getPersonneByUtilisateur(Utilisateur u) {
        return pr.findByUtilisateur(u);
    }

    public List<Personne> getListPersonneNonAdmin(int nbParPage, int noPage) {
        List<Personne> personnes = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT\r\n" + //
                    "    P.ID AS ID_PERSONNE,\r\n" + //
                    "    P.NOM AS NOM_PERSONNE,\r\n" + //
                    "    P.PRENOM AS PRENOM_PERSONNE,\r\n" + //
                    "    P.CODE_POSTAL AS CODE_POSTAL,\r\n" + //
                    "    P.CONTACT AS CONTACT,\r\n" + //
                    "    P.LOCALISATION AS LOCALISATION,\r\n" + //
                    "    P.ID_ROLE AS ID_ROLE,\r\n" + //
                    "    COALESCE(P.ID_TYPE_PRODUCTION, 0) AS ID_TYPE_PRODUCTION,\r\n" + //
                    "    P.DATE_NAISSANCE AS DATE_NAISSANCE,\r\n" + //
                    "    U.ID AS ID_UTILISATEUR,\r\n" + //
                    "    U.EMAIL AS EMAIL,\r\n" + //
                    "    U.PSEUDO AS PSEUDO,\r\n" + //
                    "    U.DATE_INSCRIPTION AS DATE_INSCRIPTION\r\n" + //
                    "FROM\r\n" + //
                    "    PERSONNE P\r\n" + //
                    "    JOIN UTILISATEUR U\r\n" + //
                    "    ON U.ID = P.ID_UTILISATEUR\r\n" + //
                    "WHERE\r\n" + //
                    "    U.IS_ADMIN = 0 LIMIT ? OFFSET ?;";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, nbParPage);
            ps.setInt(2, (noPage - 1) * nbParPage);
            ResultSet rs = ps.executeQuery();

            Personne p;
            Role role;
            TypeProduction tp;
            Utilisateur u;
            Optional<TypeProduction> optionalTp;
            while (rs.next()) {
                role = rr.findById(rs.getInt("id_role")).get();
                u = ur.findById(rs.getInt("id_utilisateur")).get();

                optionalTp = tpr.findById(rs.getInt("id_type_production"));

                if (optionalTp.isPresent()) {
                    tp = optionalTp.get();
                    p = new Personne(rs.getInt("id_personne"), rs.getString("nom_personne"),
                            rs.getString("prenom_personne"),
                            rs.getDate("date_naissance"),
                            rs.getString("code_postal"), rs.getString("contact"), rs.getString("localisation"), tp,
                            role,
                            u);
                } else {
                    p = new Personne(rs.getInt("id_personne"), rs.getString("nom_personne"),
                            rs.getString("prenom_personne"),
                            rs.getDate("date_naissance"),
                            rs.getString("code_postal"), rs.getString("contact"), rs.getString("localisation"),
                            role,
                            u);
                }
                personnes.add(p);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personnes;
    }

    public int countUser() {
        int commandes = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(p.id) as countUser FROM personne p JOIN utilisateur u ON u.id = p.id_utilisateur WHERE u.is_admin = 0;";

            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commandes = rs.getInt("countUser");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
}
