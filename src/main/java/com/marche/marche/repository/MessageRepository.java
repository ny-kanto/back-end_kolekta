package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Message;
import com.marche.marche.modele.Personne;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.dateMessage ASC")
    List<Message> findByConversation(@Param("conversation") Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.expediteur = :acheteur ORDER BY m.dateMessage ASC")
    List<Message> findByConversationAndByAcheteur(@Param("conversation") Conversation conversation, @Param("acheteur") Personne acheteur);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.expediteur = :vendeur ORDER BY m.dateMessage ASC")
    List<Message> findByConversationAndByVendeur(@Param("conversation") Conversation conversation, @Param("vendeur") Personne vendeur);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.expediteur = :acheteur AND m.statusMessage = :status")
    int messageNonLusAcheteur(@Param("conversation") Conversation conversation, @Param("acheteur") Personne acheteur, @Param("status") int status);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.expediteur = :vendeur AND m.statusMessage = :status")
    int messageNonLusVendeur(@Param("conversation") Conversation conversation, @Param("vendeur") Personne vendeur, @Param("status") int status);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.acheteur = :acheteur AND m.expediteur != :acheteur AND m.statusMessage = :status")
    int messageNonLusAcheteur(@Param("acheteur") Personne acheteur, @Param("status") int status);
}
