package com.marche.marche.services;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Commentaire;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.repository.CommentaireRepository;
import com.marche.marche.repository.PersonneRepository;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository cr;

    @Autowired
    private PersonneRepository pr;

    @Autowired
    private DataSource dataSource;

    public void saveCommentaire(Commentaire Commentaire) {
        cr.save(Commentaire);
    }

    public List<Commentaire> getCommentaireByProduit(Produit produit, int nbrParPage, int noPage) {
        // return cr.findByProduit(produit);
        List<Commentaire> commentaires = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM commentaire WHERE id_produit = ? ORDER BY date_commentaire DESC LIMIT ? OFFSET ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, produit.getId());
            ps.setInt(2, nbrParPage);
            ps.setInt(3, (noPage - 1) * nbrParPage);
            ResultSet rs = ps.executeQuery();

            Commentaire com;
            Personne personne;
            while (rs.next()) {
                personne = pr.findById(rs.getInt("id_personne")).get();
                com = new Commentaire(personne, produit, rs.getString("contenu_commentaire"), rs.getTimestamp("date_commentaire"));
                commentaires.add(com);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaires;
    }
    
    public int countCommentaire(Produit p) {
        int countCommentaire = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(*) as countCommentaire FROM commentaire where id_produit = ?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                countCommentaire = rs.getInt("countCommentaire");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countCommentaire;
    }
}
