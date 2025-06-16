package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Message;
import com.marche.marche.modele.Personne;
import com.marche.marche.repository.MessageRepository;


import java.util.List;
@Service
public class MessageService {

    @Autowired
    private MessageRepository mr;

    public void saveMessage(Message message) {
        mr.save(message);
    }

    public List<Message> getListMessagesByConversation(Conversation conversation) {
        return mr.findByConversation(conversation);
    }

    public List<Message> getListMessagesByConversationAndAcheteur(Conversation conversation, Personne acheteur) {
        return mr.findByConversationAndByAcheteur(conversation, acheteur);
    }

    public List<Message> getListMessagesByConversationAndVendeur(Conversation conversation, Personne vendeur) {
        return mr.findByConversationAndByVendeur(conversation, vendeur);
    }

    public int messageNonLusVendeur(Conversation conversation, Personne vendeur, int status) {
        return mr.messageNonLusVendeur(conversation, vendeur, status);
    }

    public int messageNonLusAcheteur(Conversation conversation, Personne acheteur, int status) {
        return mr.messageNonLusAcheteur(conversation, acheteur, status);
    }

    public int messageNonLusAcheteur(Personne acheteur, int status) {
        return mr.messageNonLusAcheteur(acheteur, status);
    }
}
