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
@Table(name = "message")
@SequenceGenerator(
    name = "message_seq", 
    sequenceName = "message_seq", 
    allocationSize = 1
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    private int id;

    @Column(name = "contenu_message")
    private String contenuMessage;

    @Column(name = "date_message")
    private Timestamp dateMessage;

    @ManyToOne
    @JoinColumn(name = "id_conversation", referencedColumnName = "id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "id_expediteur", referencedColumnName = "id")
    private Personne expediteur;

    @Column(name = "status_message")
    private int statusMessage;
    
    public Message(String contenuMessage, Timestamp dateMessage, Conversation conversation, Personne expediteur, int statusMessage) {
        this.contenuMessage = contenuMessage;
        this.dateMessage = dateMessage;
        this.conversation = conversation;
        this.expediteur = expediteur;
        this.statusMessage = statusMessage;
    }
}
