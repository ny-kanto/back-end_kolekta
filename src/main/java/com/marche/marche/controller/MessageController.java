package com.marche.marche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Conversation;
import com.marche.marche.modele.Message;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.ConversationService;
import com.marche.marche.services.MessageService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.UtilisateurService;

import java.sql.Timestamp;
import java.time.Instant;

import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/message")
@CrossOrigin(origins = "*")
public class MessageController {
    @Autowired
    private MessageService ms;

    @Autowired
    private ConversationService cs;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private PersonneService pes;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // CONTROLLEUR D'ENVOI DE MESSAGE
    @PostMapping("/save-message/{id_recepteur}")
    public ResponseEntity<APIResponse> saveMessage(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_recepteur") int idRecepteur,
            @RequestParam(name = "contenu_message") String contenuMessage) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            // List<Object> obj = new ArrayList<>();

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);

            // Utilisateur u1 = us.getUtilisateur(idRecepteur);
            Personne p1 = pes.getPersonneById(idRecepteur);

            Conversation conversation = cs.getConversationByVendeurAndAcheteur(p, p1);

            Timestamp dateMessage = Timestamp.from(Instant.now());

            Message message = new Message(contenuMessage, dateMessage, conversation, p, 0);

            ms.saveMessage(message);

            // Envoyer le message en temps réel à la destination WebSocket de l'acheteur
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(idRecepteur), // ID du recepteur
                    "/queue/messages", // Destination de l'utilisateur
                    message // Message à envoyer
            );

            // obj.add(message);

            APIResponse api = new APIResponse(null, message);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR POUR AVOIR LA LISTE DE MESSAGE
    @GetMapping("/list/{id_recepteur}")
    public ResponseEntity<APIResponse> getListMessageByConversation(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_recepteur") int idRecepteur) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            List<Object> obj = new ArrayList<>();

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);

            Personne p1 = pes.getPersonneById(idRecepteur);

            Conversation conversation = cs.getConversationByVendeurAndAcheteur(p, p1);

            if (conversation == null) {
                conversation = new Conversation(p, p1);
                cs.saveConversation(conversation);
            }

            List<Message> messagesRecepteur = ms.getListMessagesByConversationAndAcheteur(conversation, p1);
            for (int i = 0; i < messagesRecepteur.size(); i++) {
                messagesRecepteur.get(i).setStatusMessage(1);
                ms.saveMessage(messagesRecepteur.get(i));
            }

            List<Message> messages = ms.getListMessagesByConversation(conversation);

            obj.add(messages);
            obj.add(conversation);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
