package com.marche.marche.modele;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commentaire")
@SequenceGenerator(
    name = "commentaire_seq", 
    sequenceName = "commentaire_seq", 
    allocationSize = 1
)
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentaire_seq")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_personne", referencedColumnName = "id")
    private Personne personne;

    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id")
    private Produit produit;

    @Column(name = "contenu_commentaire")
    private String contenuCommentaire;

    @Column(name = "date_commentaire")
    private Timestamp dateCommentaire;

    public Commentaire(Personne personne, Produit produit, String contenuCommentaire, Timestamp dateCommentaire) {
        this.personne = personne;
        this.produit = produit;
        this.contenuCommentaire = contenuCommentaire;
        this.dateCommentaire = dateCommentaire;
    }
}
