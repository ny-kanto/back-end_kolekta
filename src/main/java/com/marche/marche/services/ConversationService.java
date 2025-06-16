package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Personne;
import com.marche.marche.repository.ConversationRepository;
import com.marche.marche.repository.PersonneRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository cr;

    @Autowired
    private PersonneRepository pr;

    @Autowired
    private DataSource dataSource;

    public void saveConversation(Conversation conversation) {
        cr.save(conversation);
    }

    public Conversation getConversationByVendeurAndAcheteur(Personne vendeur, Personne acheteur) {
        return cr.findByVendeurAndAcheteur(vendeur, acheteur);
    }

    public List<Conversation> getConversationByAcheteur(Personne acheteur) {
        // return cr.findByAcheteur(acheteur);
        List<Conversation> conversations = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();

            String sql = "SELECT c.*, COALESCE(m.contenu_message, '') AS dernier_message, COALESCE(m.id_expediteur, 0) AS dernier_expediteur FROM conversation c JOIN personne p ON p.id = c.id_vendeur\n"
                    + //
                    "LEFT JOIN message m ON m.id_conversation = c.id AND m.date_message = (\n" + //
                    "    SELECT MAX(m2.date_message)\n" + //
                    "    FROM message m2\n" + //
                    "    WHERE m2.id_conversation = c.id\n" + //
                    ")\n" + //
                    "WHERE c.id_acheteur = ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, acheteur.getId());

            System.out.println("sql : " + sql);

            ResultSet rs = ps.executeQuery();
            Conversation co;
            Personne vendeur;
            while (rs.next()) {
                vendeur = pr.findById(rs.getInt("id_vendeur")).get();
                co = new Conversation(rs.getInt("id"), vendeur, acheteur);
                co.setDernierMessage(rs.getString("dernier_message"));
                co.setDernierExpediteur(rs.getInt("dernier_expediteur"));
                conversations.add(co);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }

    public List<Conversation> getConversationByAcheteur(Personne p, String recherche) {
        List<Conversation> conversations = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();

            // Diviser la recherche en plusieurs mots
            String[] motsRecherche = recherche.toLowerCase().split(" ");
            String sql = "SELECT c.*, COALESCE(m.contenu_message, '') AS dernier_message, COALESCE(m.id_expediteur, 0) AS dernier_expediteur FROM conversation c JOIN personne p ON p.id = c.id_vendeur\n"
                    + //
                    "LEFT JOIN message m ON m.id_conversation = c.id AND m.date_message = (\n" + //
                    "    SELECT MAX(m2.date_message)\n" + //
                    "    FROM message m2\n" + //
                    "    WHERE m2.id_conversation = c.id\n" + //
                    ")\n" + //
                    "WHERE c.id_acheteur = ?";

            StringBuilder likeClause = new StringBuilder();
            for (String mot : motsRecherche) {
                if (likeClause.length() > 0) {
                    likeClause.append(" AND ");
                }
                likeClause.append("(LOWER(p.prenom) LIKE ? OR LOWER(p.nom) LIKE ?)");
            }

            sql += " AND (" + likeClause.toString() + ")";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, p.getId());

            System.out.println("sql : " + sql);

            int paramIndex = 2;
            for (String mot : motsRecherche) {
                ps.setString(paramIndex++, "%" + mot + "%");
                ps.setString(paramIndex++, "%" + mot + "%");
            }

            ResultSet rs = ps.executeQuery();
            Conversation co;
            Personne vendeur;
            while (rs.next()) {
                vendeur = pr.findById(rs.getInt("id_vendeur")).get();
                co = new Conversation(rs.getInt("id"), vendeur, p);
                co.setDernierMessage(rs.getString("dernier_message"));
                co.setDernierExpediteur(rs.getInt("dernier_expediteur"));
                conversations.add(co);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }

}
