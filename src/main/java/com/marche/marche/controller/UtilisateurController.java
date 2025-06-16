package com.marche.marche.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
// import com.marche.marche.modele.NotificationMessage;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.Role;
import com.marche.marche.modele.TypeProduction;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.EvaluationService;
// import com.marche.marche.services.FirebaseMessagingService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.RoleService;
import com.marche.marche.services.TypeProductionService;
import com.marche.marche.services.UtilisateurService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UtilisateurController {
    @Autowired
    private PersonneService ps;

    @Autowired
    private ProduitService prs;

    @Autowired
    private EvaluationService evs;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private RoleService rs;

    @Autowired
    private TypeProductionService tps;

    @Autowired
    private JwtUtil jwtUtil;

    // @Autowired
    // private FirebaseMessagingService fbs;

    // @PutMapping("/update-fcm-token")
    // public ResponseEntity<?> updateFcmToken(@RequestHeader("Authorization") String authorizationHeader,
    //         @RequestBody Map<String, String> requestBody) {
    //     String fcmToken = requestBody.get("fcmToken");
    //     int idUtilisateur = 0;
    //     if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
    //         String token = authorizationHeader.substring(7);
    //         Claims claims = jwtUtil.parseJwtClaims(token);
    //         idUtilisateur = JwtUtil.getUserId(claims);
    //     }

    //     Utilisateur utilisateur = us.getUtilisateur(idUtilisateur);
    //     Personne personne = ps.getPersonneByUtilisateur(utilisateur);
    //     personne.setToken(fcmToken);
    //     ps.savePersonne(personne);

    //     return ResponseEntity.ok("FCM Token mis à jour avec succès");
    // }

    // @PostMapping("/send-notification")
    // public String sendNotificationbyToken(@RequestBody NotificationMessage notificationMessage) {
    //     return fbs.sendNotificationByToken(notificationMessage);
    // }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse> signup(@RequestParam("nom") String nom, @RequestParam("prenom") String prenom,
            @RequestParam("pseudo") String pseudo,
            @RequestParam("email") String email, @RequestParam("password") String password,
            @RequestParam("date_naissance") Date dateNaissance, @RequestParam("code_postal") String codePostal,
            @RequestParam("id_role") int idRole,
            @RequestParam(name = "id_type_production", required = false) int idTypeProduction,
            @RequestParam("contact") String contact, @RequestParam("localisation") String localisation) {

        try {
            LocalDate currentDate = LocalDate.now();
            Date dateInscription = Date.valueOf(currentDate);
            Utilisateur u = new Utilisateur(pseudo, email, password, dateInscription);
            us.saveUtilisateur(u);
            Role role = rs.getRoleById(idRole);
            TypeProduction tp = null;
            if (idTypeProduction != 0) {
                tp = tps.getTypeProductionById(idTypeProduction);
            }

            Personne p = new Personne(nom, prenom, dateNaissance, codePostal, contact, localisation, tp, role, u);

            APIResponse api = new APIResponse(null, p);
            ps.savePersonne(p);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur message : " + e.getMessage());
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/new-admin")
    public ResponseEntity<APIResponse> signup(@RequestParam("pseudo") String pseudo,
            @RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            LocalDate currentDate = LocalDate.now();
            Date dateInscription = Date.valueOf(currentDate);
            Utilisateur u = new Utilisateur(pseudo, email, password, dateInscription, 1);
            us.saveUtilisateur(u);

            APIResponse api = new APIResponse(null, u);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur message : " + e.getMessage());
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<APIResponse> getCountPanier(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            List<Object> obj = new ArrayList<>();
            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = ps.getPersonneByUtilisateur(u);

            obj.add(p);
            obj.add(u);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse> getListUser(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "15") int nbrParPage, @RequestParam(defaultValue = "1") int noPage) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            double totalPages = 0;
            List<Object> obj = new ArrayList<>();
            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = ps.getPersonneByUtilisateur(u);
            List<Personne> personnes = ps.getListPersonneNonAdmin(nbrParPage, noPage);
            totalPages = (int) Math.ceil((double) ps.countUser() / nbrParPage);

            obj.add(p);
            obj.add(u);
            obj.add(personnes);
            obj.add(noPage);
            obj.add(totalPages);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/profile-vendeur/{id_vendeur}")
    public ResponseEntity<APIResponse> getProfileVendeur(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_vendeur") int idVendeur) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            // {
            // String token = authorizationHeader.substring(7);
            // Claims claims = jwtUtil.parseJwtClaims(token);
            // idUtilisateur = JwtUtil.getUserId(claims);
            // }

            List<Object> obj = new ArrayList<>();
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = ps.getPersonneByUtilisateur(u);

            Personne p2 = ps.getPersonneById(idVendeur);

            List<Produit> produits = prs.getAllProduitsByPersonne(p2);
            List<Produit> produits2 = prs.getProduitNoteStockByPersonne(p2);

            double totalNotes = 0;
            int totalEvaluations = 0;
            int totalCount;
            double weightedSum = 0;
            Map<Integer, Long> evaluationCountNote = null;
            for (Produit produit : produits) {
                evaluationCountNote = evs.getEvaluationCountsByNote(produit);
                totalCount = 0;
                weightedSum = 0;

                for (Map.Entry<Integer, Long> entry : evaluationCountNote.entrySet()) {
                    totalCount += entry.getValue();
                    weightedSum += entry.getKey() * entry.getValue();
                }

                if (totalCount > 0) {
                    totalNotes += weightedSum;
                    totalEvaluations += totalCount;
                }
            }

            double noteMoyenne = totalEvaluations > 0 ? totalNotes / totalEvaluations : 0;

            obj.add(p2);
            obj.add(produits2);
            obj.add(noteMoyenne);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/profile-acheteur/{id_acheteur}")
    public ResponseEntity<APIResponse> getProfileAcheteur(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_acheteur") int idAcheteur) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            // {
            // String token = authorizationHeader.substring(7);
            // Claims claims = jwtUtil.parseJwtClaims(token);
            // idUtilisateur = JwtUtil.getUserId(claims);
            // }

            List<Object> obj = new ArrayList<>();
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = ps.getPersonneByUtilisateur(u);

            Personne p2 = ps.getPersonneById(idAcheteur);

            obj.add(p2);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
