package com.marche.marche.modele;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation")
@SequenceGenerator(
    name = "conversation_seq", 
    sequenceName = "conversation_seq", 
    allocationSize = 1
)
public class Conversation {
    public Conversation(int id, Personne vendeur, Personne acheteur) {
        this.id = id;
        this.vendeur = vendeur;
        this.acheteur = acheteur;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conversation_seq")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_vendeur", referencedColumnName = "id")
    private Personne vendeur;

    @ManyToOne
    @JoinColumn(name = "id_acheteur", referencedColumnName = "id")
    private Personne acheteur;

    @Transient
    private int messageNonLus;

    @Transient
    private String dernierMessage;

    @Transient
    private int dernierExpediteur;

    public Conversation(Personne vendeur, Personne acheteur) {
        this.vendeur = vendeur;
        this.acheteur = acheteur;
    }
}
