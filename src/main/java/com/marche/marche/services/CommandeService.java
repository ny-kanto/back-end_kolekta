package com.marche.marche.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Commande;
import com.marche.marche.modele.CommandeProduit;
import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.repository.CommandeProduitRepository;
import com.marche.marche.repository.CommandeRepository;
import com.marche.marche.repository.ConversationRepository;
import com.marche.marche.repository.MessageRepository;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.ProduitRepository;

@Service
public class CommandeService {
    @Autowired
    private CommandeRepository cr;

    @Autowired
    private CommandeProduitRepository cpr;

    @Autowired
    private ProduitRepository pr;

    @Autowired
    private PersonneRepository per;
    
    @Autowired
    private ConversationRepository cor;

    @Autowired
    private MessageRepository mr;

    @Autowired
    private DataSource dataSource;

    public void saveCommande(Commande commande) {
        cr.save(commande);
    }

    public void saveCommandeProduit(CommandeProduit commandeProduit) {
        cpr.save(commandeProduit);
    }

    public CommandeProduit getCommandeProduitByCommandeAndProduit(Commande commande, Produit produit) {
        return cpr.getCommandeProduitByCommandeAndProduit(commande, produit);
    }

    public Commande getCommandeByDateCommandeAndMontantTotalAndAdresseLivraisonAndNumClientAndPersonne(Timestamp dateCommande, double montantTotal, String adresseLivraison, String numClient, Personne personne) {
        return cr.findByDateCommandeAndMontantTotalAndAdresseLivraisonAndNumClientAndPersonne(dateCommande, montantTotal, adresseLivraison, numClient, personne);
    }

    public List<CommandeProduit> listCommandeProduitByVendeur(Personne personne, Commande commande, int nbrParPage, int noPage,
            int status) {
        List<CommandeProduit> commandeProduits = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit JOIN commande co ON co.id = cp.id_commande WHERE pro.id_personne = ? AND co.id = ?";

            if (status != -1) {
                sql += " AND cp.status_commande = ?";
            }

            sql += " ORDER BY co.date_commande ASC, cp.id_produit ASC LIMIT ? OFFSET ?";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            int parameterIndex = 1;
            ps.setInt(parameterIndex++, personne.getId());
            ps.setInt(parameterIndex++, commande.getId());

            if (status != -1) {
                ps.setInt(parameterIndex++, status);
            }

            ps.setInt(parameterIndex++, nbrParPage);
            ps.setInt(parameterIndex, (noPage - 1) * nbrParPage);

            System.out.println("sql: ");
            ResultSet rs = ps.executeQuery();

            CommandeProduit es;
            Produit produit;
            while (rs.next()) {
                es = new CommandeProduit();
                produit = pr.findById(rs.getInt("id_produit")).get();
                es.setCommande(commande);
                es.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                es.setProduit(produit);
                es.setQuantite(rs.getDouble("quantite"));
                es.setStatusCommande(rs.getInt("status_commande"));
                commandeProduits.add(es);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeProduits;
    }

    public List<CommandeProduit> listCommandeProduitByVendeur(Personne personne) {
        List<CommandeProduit> commandeProduits = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit JOIN commande co ON co.id = cp.id_commande WHERE pro.id_personne = ? ORDER BY co.date_commande DESC, cp.id_produit ASC";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());

            System.out.println("sql: ");
            ResultSet rs = ps.executeQuery();

            CommandeProduit es;
            Produit produit;
            Commande commande;
            while (rs.next()) {
                es = new CommandeProduit();
                produit = pr.findById(rs.getInt("id_produit")).get();
                commande = cr.findById(rs.getInt("id_commande")).get();
                es.setCommande(commande);
                es.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                es.setProduit(produit);
                es.setQuantite(rs.getDouble("quantite"));
                es.setStatusCommande(rs.getInt("status_commande"));
                es.setTotal(es.getQuantite() * es.getPrixUnitaire());
                commandeProduits.add(es);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeProduits;
    }

    public List<Commande> listCommandeByVendeur(Personne personne, int nbrParPage, int noPage) {
        List<Commande> commandes = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT C.* FROM COMMANDE C JOIN COMMANDE_PRODUIT CP ON CP.ID_COMMANDE = C.ID JOIN PRODUIT P ON P.ID = CP.ID_PRODUIT JOIN PERSONNE PE ON PE.ID = P.ID_PERSONNE WHERE P.ID_PERSONNE = ? GROUP BY C.ID ORDER BY C.DATE_COMMANDE DESC LIMIT ? OFFSET ?;";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ps.setInt(2, nbrParPage);
            ps.setInt(3, (noPage - 1) * nbrParPage);

            System.out.println("sql: ");
            ResultSet rs = ps.executeQuery();

            Commande commande;
            Personne client;
            Conversation conversation;
            while (rs.next()) {
                client = per.findById(rs.getInt("id_personne")).get();
                conversation = cor.findByVendeurAndAcheteur(personne, client);
                commande = new Commande(rs.getInt("id"), rs.getTimestamp("date_commande"), rs.getDouble("montant_total"), rs.getString("adresse_livraison"), rs.getString("num_client"), client);
                commande.setMessageNonLus(mr.messageNonLusAcheteur(conversation, client, 0));
                commande.setMontantTva(commande.getMontantTotal() * 0.2);
                commande.setMontantTtc(commande.getMontantTotal() + commande.getMontantTva());
                commandes.add(commande);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public int countCommandeByVendeur(Personne personne, int nbrParPage, int noPage) {
        int commandes = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(C.*) as countCommande FROM COMMANDE C JOIN COMMANDE_PRODUIT CP ON CP.ID_COMMANDE = C.ID JOIN PRODUIT P ON P.ID = CP.ID_PRODUIT JOIN PERSONNE PE ON PE.ID = P.ID_PERSONNE WHERE P.ID_PERSONNE = ? GROUP BY C.ID ORDER BY C.DATE_COMMANDE DESC LIMIT ? OFFSET ?;";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, personne.getId());
            ps.setInt(2, nbrParPage);
            ps.setInt(3, (noPage - 1) * nbrParPage);

            System.out.println("sql: ");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commandes = rs.getInt("countCommande");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public Commande getCommandeById(int id) {
        return cr.findById(id).get();
    }

    public int countCommande(int idPersonne, int status) {
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT distinct cp.id_commande as countCommande FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit WHERE pro.id_personne = ?";

            if (status != -1) {
                sql += " AND cp.status_commande = ?";
            }

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);

            if (status != -1) {
                ps.setInt(2, status);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // countCommande = rs.getInt("countCommande");
                countCommande++;
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return countCommande;
    }

    public int countCommandeNLAndEC(int idPersonne) {
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT distinct cp.id_commande as countCommande FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit WHERE pro.id_personne = ? AND cp.status_commande = 0 OR pro.id_personne = ? AND cp.status_commande = 1";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);
            ps.setInt(2, idPersonne);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // countCommande = rs.getInt("countCommande");
                countCommande++;
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return countCommande;
    }

    public List<Integer> idCommandeNonLivree(int idPersonne) {
        List<Integer> idCommandeNonLivree = new ArrayList<>();
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT distinct cp.id_commande as countCommande FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit WHERE pro.id_personne = ? AND cp.status_commande = 1";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                countCommande = rs.getInt("countCommande");
                idCommandeNonLivree.add(countCommande);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return idCommandeNonLivree;
    }

    public List<Integer> idCommandeEnCours(int idPersonne) {
        List<Integer> idCommandeEnCours = new ArrayList<>();
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT distinct cp.id_commande as countCommande FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit WHERE pro.id_personne = ? AND cp.status_commande = 0";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                countCommande = rs.getInt("countCommande");
                idCommandeEnCours.add(countCommande);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return idCommandeEnCours;
    }

    public int countCommandeProduit(int idPersonne, int idCommande, int status) {
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(*) as countCommande FROM commande_produit cp JOIN produit pro ON pro.id = cp.id_produit";

            sql += " WHERE pro.id_personne = ? AND cp.id_commande = ?";

            if (status != -1) {
                sql += " AND cp.status_commande = ?";
            }

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);
            ps.setInt(2, idCommande);

            if (status != -1) {
                ps.setInt(3, status);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                countCommande = rs.getInt("countCommande");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return countCommande;
    }

    public int count(int idPersonne) {
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(*) as countCommande FROM commande c JOIN commande_produit cp ON cp.id_commande = c.id JOIN produit pro ON pro.id = cp.id_produit WHERE pro.id_personne = ? GROUP BY c.id";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, idPersonne);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                countCommande++;
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return countCommande;
    }

    public List<Commande> listCommandeByAdmin(int nbrParPage, int noPage) {
        List<Commande> commandes = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT * FROM COMMANDE C WHERE C.METHODE_PAIEMENT = 1 ORDER BY C.DATE_COMMANDE DESC LIMIT ? OFFSET ?;";

            System.out.println("sql : " + sql);
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, nbrParPage);
            ps.setInt(2, (noPage - 1) * nbrParPage);
            ResultSet rs = ps.executeQuery();

            Commande commande;
            Personne client;
            while (rs.next()) {
                client = per.findById(rs.getInt("id_personne")).get();
                commande = new Commande(rs.getInt("id"), rs.getTimestamp("date_commande"), rs.getDouble("montant_total"), rs.getString("adresse_livraison"), rs.getString("num_client"), client);
                commande.setMontantTva(commande.getMontantTotal() * 0.2);
                commande.setMontantTtc(commande.getMontantTotal() + commande.getMontantTva());
                commande.setMethodePaiement(rs.getInt("methode_paiement"));
                commande.setReference(rs.getString("reference"));
                commande.setStatus(rs.getInt("status"));
                commandes.add(commande);
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public int countCommandeMobile() {
        int countCommande = 0;
        try {
            Connection c = dataSource.getConnection();
            String sql = "SELECT count(*) as countCommande FROM commande c WHERE c.methode_paiement = 1";

            PreparedStatement ps = c.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                countCommande = rs.getInt("countCommande");
            }

            rs.close();
            ps.close();
            c.close();

        } catch (SQLException p) {
            p.printStackTrace();
        }
        return countCommande;
    }
}
