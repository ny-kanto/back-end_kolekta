package com.marche.marche.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Commande;
import com.marche.marche.modele.CommandeProduit;
import com.marche.marche.modele.EtatStock;
import com.marche.marche.modele.NotificationMessage;
import com.marche.marche.modele.Panier;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPanier;
import com.marche.marche.modele.ProduitPhotos;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.modele.response.ErrorRes;
import com.marche.marche.services.CommandeService;
import com.marche.marche.services.EntreeService;
// import com.marche.marche.services.FirebaseMessagingService;
import com.marche.marche.services.MessageService;
import com.marche.marche.services.PanierService;
import com.marche.marche.services.PaymentService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitPhotosService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.UtilisateurService;
import com.marche.marche.utils.Utils;
import com.stripe.model.checkout.Session;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/panier")
@CrossOrigin(origins = "*")
public class PanierController {
    @Autowired
    private PanierService pas;

    @Autowired
    private ProduitService pros;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProduitPhotosService pps;

    @Autowired
    private PersonneService pes;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private EntreeService es;

    @Autowired
    private CommandeService cs;

    @Autowired
    private MessageService ms;

    @Autowired
    private JwtUtil jwtUtil;

    // CONTROLLEUR D'AJOUT DE PRODUIT DANS PANIER DE L'ACHETEUR
    @SuppressWarnings("rawtypes")
    @PostMapping("/save/{id_produit}")
    public ResponseEntity saveProduitPanier(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_produit") int idProduit, @RequestParam String quantite) {
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

            EtatStock etatStock = es.getEtatStockByIdProduit(idProduit);

            Panier panier = pas.getPanierByPersonne(p);

            if (panier == null) {
                panier = new Panier(p);
                pas.savePanier(panier);
            }

            Produit produit = pros.getProduit(idProduit);

            if (Integer.parseInt(quantite) < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorRes(HttpStatus.BAD_REQUEST, "Veuillez entrer une quantité positive"));
            }

            if (etatStock.getReste() < Integer.parseInt(quantite)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorRes(HttpStatus.BAD_REQUEST, "Quantité dans le stock manquant"));
            }

            ProduitPanier pp = pas.saveProduitPanier(new ProduitPanier(produit, panier, Double.valueOf(quantite)));

            obj.add(pp);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse> getAllOrder(@RequestHeader(name = "Authorization") String authorizationHeader) {
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
            Panier panier = pas.getPanierByPersonne(p);

            if (panier == null) {
                panier = new Panier(p);
                pas.savePanier(panier);
            }

            List<ProduitPanier> pp = pas.listProduitPanier(panier);

            Map<String, String> photoData = null;
            ProduitPhotos photos = null;
            String mimeType = "";
            HashMap<Integer, Map<String, String>> produitData = new HashMap<>();
            Produit produit = null;

            List<Map<String, Object>> produitsAvecTotal = new ArrayList<>();
            double total = 0;
            Map<String, Object> produitDetails = null;
            double tva = 0;

            double totalGlobal = 0;
            double tvaGlobal = 0;

            for (ProduitPanier produitPanier : pp) {
                produit = produitPanier.getProduit();
                photos = pps.getOneProduitPhotosByProduit(produit);

                total = produitPanier.getQuantite() * produitPanier.getProduit().getPrix();
                tva = total * 0.2;

                produitDetails = new HashMap<>();
                produitDetails.put("id", produit.getId());
                produitDetails.put("nom", produit.getNom());
                produitDetails.put("prix", produit.getPrix());
                produitDetails.put("quantite", produitPanier.getQuantite());
                produitDetails.put("total", total);
                produitDetails.put("tva", tva);
                produitDetails.put("id_vendeur", produit.getPersonne().getId());
                produitDetails.put("nom_vendeur", produit.getPersonne().getNom());
                produitDetails.put("prenom_vendeur", produit.getPersonne().getPrenom());
                produitDetails.put("pseudo_vendeur", produit.getPersonne().getUtilisateur().getPseudo());
                produitDetails.put("email_vendeur", produit.getPersonne().getUtilisateur().getEmail());
                produitDetails.put("contact_vendeur", produit.getPersonne().getContact());
                produitsAvecTotal.add(produitDetails);

                totalGlobal += total;
                tvaGlobal += tva;

                if (photos != null) {
                    mimeType = Utils.guessMimeType(photos.getPhotos());
                    photoData = new HashMap<>();
                    photoData.put("base64", Base64.getEncoder().encodeToString(photos.getPhotos()));
                    photoData.put("mimeType", mimeType);
                    produitData.put(produit.getId(), photoData);
                }
            }

            double ttc = totalGlobal + tvaGlobal;

            List<Panier> paniers = pas.getListPrixByPanier(panier.getId());

            obj.add(produitsAvecTotal);
            obj.add(produitData);
            obj.add(paniers);
            obj.add(p);
            obj.add(totalGlobal);
            obj.add(tvaGlobal);
            obj.add(ttc);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/count")
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
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            if (panier == null) {
                panier = new Panier(p);
                pas.savePanier(panier);
            }

            int count = pas.countProduitPanier(panier);
            int messageNonLus = ms.messageNonLusAcheteur(p, 0);

            obj.add(count);
            obj.add(p);
            obj.add(messageNonLus);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id_produit}")
    public ResponseEntity<APIResponse> deletePanierProduit(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_produit") int idProduit) {
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
            Panier panier = pas.getPanierByPersonne(p);
            Produit produit = pros.getProduit(idProduit);

            if (panier == null) {
                panier = new Panier(p);
                pas.savePanier(panier);
            }

            pas.deleteProduitPanier(panier, produit);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @SuppressWarnings("rawtypes")
    @PutMapping("/update/{id_produit}")
    public ResponseEntity updatePanierProduit(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_produit") int idProduit,
            @RequestBody Map<String, Double> requestBody) {
        try {
            Double quantite = requestBody.get("quantite");

            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Produit produit = pros.getProduit(idProduit);
            EtatStock ets = es.getEtatStockByIdProduit(idProduit);

            if (ets.getReste() < quantite) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorRes(HttpStatus.BAD_REQUEST, "Quantité dans le stock manquant"));
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            if (panier == null) {
                panier = new Panier(p);
                pas.savePanier(panier);
            }

            pas.updateProduitPanier(quantite, panier, produit);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/checkout-payment")
    public ResponseEntity<APIResponse> checkoutPayment(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            List<ProduitPanier> pp = pas.listProduitPanier(panier);

            double montantTotal = 0;

            for (int i = 0; i < pp.size(); i++) {
                montantTotal += (pp.get(i).getQuantite() * pp.get(i).getProduit().getPrix());
            }

            double tva = montantTotal * 0.2;
            double ttc = montantTotal + tva;

            Long amount = (long) ttc;
            Session session = paymentService.createCheckoutSession(amount);

            // notifySeller(pp.get(0).getProduit().getPersonne().getNom(), p.getNom(), "Vous
            // avez reçu une nouvelle commande.");

            // for (ProduitPanier produitPanier : pp) {
            // notificationService.sendNotification("/topic/vendeur/" +
            // produitPanier.getProduit().getPersonne().getId(),
            // "Nouvelle commande pour votre produit !");
            // }

            APIResponse api = new APIResponse(null, session.getId());
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/save-commande")
    public ResponseEntity<APIResponse> saveCommande(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(name = "adresse_livraison") String adresseLivraison) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            List<ProduitPanier> pp = pas.listProduitPanier(panier);

            Commande commande;
            Timestamp dateCommande = Timestamp.from(Instant.now());
            double montantTotal = 0;

            for (int i = 0; i < pp.size(); i++) {
                montantTotal += (pp.get(i).getQuantite() * pp.get(i).getProduit().getPrix());
            }

            double tva = montantTotal * 0.2;
            double ttc = montantTotal + tva;

            LocalDate dateCommandeLocalDate = dateCommande.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dateFormatee = dateCommandeLocalDate.format(formatter);

            // String adresseLivraison = "Antananarivo";
            String numClient = "C" + p.getId() + "/" + dateFormatee;

            // notifySeller(pp.get(0).getProduit().getPersonne().getNom(), p.getNom(), "Vous
            // avez reçu une nouvelle commande.");

            commande = new Commande(dateCommande, ttc, adresseLivraison, numClient, p);
            commande.setReference("");
            commande.setStatus(0);
            commande.setMethodePaiement(0);
            cs.saveCommande(commande);

            CommandeProduit commandeProduit;
            for (int i = 0; i < pp.size(); i++) {
                commandeProduit = new CommandeProduit(commande, pp.get(i).getProduit(),
                        pp.get(i).getQuantite(),
                        pp.get(i).getProduit().getPrix(), 0);
                cs.saveCommandeProduit(commandeProduit);
            }

            // for (ProduitPanier produitPanier : pp) {
            // notificationService.sendNotification("/topic/vendeur/" +
            // produitPanier.getProduit().getPersonne().getId(),
            // "Nouvelle commande pour votre produit !");
            // }

            pas.deleteProduitPanier(panier);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<APIResponse> PaymentIntent(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            List<ProduitPanier> pp = pas.listProduitPanier(panier);

            Commande commande;
            Timestamp dateCommande = Timestamp.from(Instant.now());
            double montantTotal = 0;

            for (int i = 0; i < pp.size(); i++) {
                montantTotal += (pp.get(i).getQuantite() * pp.get(i).getProduit().getPrix());
            }

            double tva = montantTotal * 0.2;
            double ttc = montantTotal + tva;

            LocalDate dateCommandeLocalDate = dateCommande.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dateFormatee = dateCommandeLocalDate.format(formatter);

            String adresseLivraison = "Antananarivo";
            String numClient = "C" + p.getId() + "/" + dateFormatee;

            Long amount = (long) ttc;
            String clientSecret = paymentService.createPaymentIntent(amount);

            // notifySeller(pp.get(0).getProduit().getPersonne().getNom(), p.getNom(), "Vous
            // avez reçu une nouvelle commande.");

            commande = new Commande(dateCommande, ttc, adresseLivraison, numClient, p);
            // cs.saveCommande(commande);

            // CommandeProduit commandeProduit;
            // for (int i = 0; i < pp.size(); i++) {
            // commandeProduit = new CommandeProduit(commande, pp.get(i).getProduit(),
            // pp.get(i).getQuantite(),
            // pp.get(i).getProduit().getPrix(), 0);
            // cs.saveCommandeProduit(commandeProduit);
            // }

            // for (ProduitPanier produitPanier : pp) {
            // notificationService.sendNotification("/topic/vendeur/" +
            // produitPanier.getProduit().getPersonne().getId(),
            // "Nouvelle commande pour votre produit !");
            // }

            // pas.deleteProduitPanier(panier);

            APIResponse api = new APIResponse(null, clientSecret);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/save-commande-mobile")
    public ResponseEntity<APIResponse> saveCommandeMobile(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(name = "adresse_livraison") String adresseLivraison, @RequestParam String reference) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Panier panier = pas.getPanierByPersonne(p);

            List<ProduitPanier> pp = pas.listProduitPanier(panier);

            Commande commande;
            Timestamp dateCommande = Timestamp.from(Instant.now());
            double montantTotal = 0;

            for (int i = 0; i < pp.size(); i++) {
                montantTotal += (pp.get(i).getQuantite() * pp.get(i).getProduit().getPrix());
            }

            double tva = montantTotal * 0.2;
            double ttc = montantTotal + tva;

            LocalDate dateCommandeLocalDate = dateCommande.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dateFormatee = dateCommandeLocalDate.format(formatter);

            // String adresseLivraison = "Antananarivo";
            String numClient = "C" + p.getId() + "/" + dateFormatee;

            // notifySeller(pp.get(0).getProduit().getPersonne().getNom(), p.getNom(), "Vous
            // avez reçu une nouvelle commande.");

            commande = new Commande(dateCommande, ttc, adresseLivraison, numClient, p);
            commande.setReference(reference);
            commande.setStatus(0);
            commande.setMethodePaiement(1);
            cs.saveCommande(commande);

            CommandeProduit commandeProduit;
            for (int i = 0; i < pp.size(); i++) {
                commandeProduit = new CommandeProduit(commande, pp.get(i).getProduit(),
                        pp.get(i).getQuantite(),
                        pp.get(i).getProduit().getPrix(), 0);
                cs.saveCommandeProduit(commandeProduit);
            }

            // for (ProduitPanier produitPanier : pp) {
            // notificationService.sendNotification("/topic/vendeur/" +
            // produitPanier.getProduit().getPersonne().getId(),
            // "Nouvelle commande pour votre produit !");
            // }

            pas.deleteProduitPanier(panier);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
