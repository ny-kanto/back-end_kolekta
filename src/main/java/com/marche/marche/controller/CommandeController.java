package com.marche.marche.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Commande;
import com.marche.marche.modele.CommandeProduit;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPhotos;
import com.marche.marche.modele.Sortie;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.CommandeService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitPhotosService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.SortieService;
import com.marche.marche.services.UtilisateurService;
import com.marche.marche.utils.Utils;

import io.jsonwebtoken.Claims;

import java.sql.Timestamp;

@RestController
@RequestMapping("/commande")
@CrossOrigin(origins = "*")
public class CommandeController {

    @Autowired
    private CommandeService cos;

    @Autowired
    private ProduitPhotosService pps;

    @Autowired
    private PersonneService pes;

    @Autowired
    private ProduitService ps;

    @Autowired
    private SortieService ss;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list-produit/{id_commande}")
    public ResponseEntity<APIResponse> getAllCommandeProduit(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "5") int nbrParPage, @RequestParam(defaultValue = "1") int noPage,
            @RequestParam(defaultValue = "-1") int status, @PathVariable(name = "id_commande") int idCommande) {
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
            List<CommandeProduit> cp = new ArrayList<>();

            int totalPages = 0;

            Commande commande = cos.getCommandeById(idCommande);
            cp = cos.listCommandeProduitByVendeur(p, commande, nbrParPage, noPage, status);
            totalPages = (int) Math.ceil((double) cos.countCommandeProduit(p.getId(), idCommande, status) / nbrParPage);

            double totalGlobal = 0;

            Map<String, String> photoData = null;
            ProduitPhotos photos = null;
            String mimeType = "";
            HashMap<Integer, Map<String, String>> produitData = new HashMap<>();
            Produit produit = null;

            int count = cos.countCommandeProduit(idUtilisateur, idCommande, -1);
            int countLivree = cos.countCommandeProduit(idUtilisateur, idCommande, 11);
            int countNonLivree = cos.countCommandeProduit(idUtilisateur, idCommande, 0);
            int countEnCours = cos.countCommandeProduit(idUtilisateur, idCommande, 1);

            List<Map<String, Object>> produitsAvecTotal = new ArrayList<>();
            double total = 0;
            Map<String, Object> produitDetails = null;

            for (CommandeProduit commandeProduit : cp) {
                produit = commandeProduit.getProduit();
                commande = commandeProduit.getCommande();
                photos = pps.getOneProduitPhotosByProduit(produit);

                total = commandeProduit.getQuantite() * commandeProduit.getPrixUnitaire();

                produitDetails = new HashMap<>();
                produitDetails.put("id_commande", commande.getId());
                produitDetails.put("id", produit.getId());
                produitDetails.put("nom", produit.getNom());
                produitDetails.put("prix", commandeProduit.getPrixUnitaire());
                produitDetails.put("quantite", commandeProduit.getQuantite());
                produitDetails.put("unite", produit.getUnite().getNom());
                produitDetails.put("total", total);
                produitDetails.put("nom_vendeur", produit.getPersonne().getNom());
                produitDetails.put("prenom_vendeur", produit.getPersonne().getPrenom());
                produitDetails.put("pseudo_vendeur", produit.getPersonne().getUtilisateur().getPseudo());
                produitDetails.put("email_vendeur", produit.getPersonne().getUtilisateur().getEmail());
                produitDetails.put("contact_vendeur", produit.getPersonne().getContact());
                produitDetails.put("id_acheteur", commande.getPersonne().getId());
                produitDetails.put("nom_acheteur", commande.getPersonne().getNom());
                produitDetails.put("prenom_acheteur", commande.getPersonne().getPrenom());
                produitDetails.put("pseudo_vendeur", commande.getPersonne().getUtilisateur().getPseudo());
                produitDetails.put("contact_acheteur", commande.getPersonne().getContact());
                produitDetails.put("status", commandeProduit.getStatusCommande());
                produitsAvecTotal.add(produitDetails);

                totalGlobal += total;

                if (photos != null) {
                    mimeType = Utils.guessMimeType(photos.getPhotos());
                    photoData = new HashMap<>();
                    photoData.put("base64", Base64.getEncoder().encodeToString(photos.getPhotos()));
                    photoData.put("mimeType", mimeType);
                    produitData.put(produit.getId(), photoData);
                }
            }

            obj.add(produitsAvecTotal);
            obj.add(produitData);
            obj.add(totalGlobal);
            obj.add(noPage);
            obj.add(totalPages);
            obj.add(count);
            obj.add(countLivree);
            obj.add(countNonLivree);
            obj.add(countEnCours);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse> getAllCommande(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "10") int nbrParPage, @RequestParam(defaultValue = "1") int noPage) {
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
            List<Commande> commandes = new ArrayList<>();
            List<Integer> idCommandeNonLivree = cos.idCommandeNonLivree(p.getId());
            List<Integer> idCommandeEnCours = cos.idCommandeEnCours(p.getId());
            List<CommandeProduit> cp2 = cos.listCommandeProduitByVendeur(p);
            int totalPages = 0;

            commandes = cos.listCommandeByVendeur(p, nbrParPage, noPage);
            totalPages = (int) Math.ceil((double) cos.count(p.getId()) / nbrParPage);

            obj.add(commandes);
            obj.add(noPage);
            obj.add(totalPages);
            obj.add(idCommandeNonLivree);
            obj.add(idCommandeEnCours);
            obj.add(cp2);
            obj.add(p);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<APIResponse> getCountCommande(
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
            // int count = cos.countCommande(p.getId(), 0);
            // count += cos.countCommande(p.getId(), 1);

            int count = cos.countCommandeNLAndEC(p.getId());

            obj.add(p);
            obj.add(u);
            obj.add(count);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update_status/{id_commande}/{id_produit}")
    public ResponseEntity<APIResponse> updateStatus(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_commande") int idCommande, @PathVariable(name = "id_produit") int idProduit,
            @RequestParam int status) {
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
            Commande commande = cos.getCommandeById(idCommande);
            Produit produit = ps.getProduit(idProduit);
            CommandeProduit commandeProduit = cos.getCommandeProduitByCommandeAndProduit(commande, produit);

            commandeProduit.setStatusCommande(status);

            if (status == 1) {
                Timestamp dateSortie = Timestamp.valueOf(commandeProduit.getCommande().getDateCommande().toString());
                Sortie sortie = ss.getSortieByProduitAndQuantiteAndDateSortie(produit, commandeProduit.getQuantite(),
                        dateSortie);

                if (sortie == null) {
                    sortie = new Sortie(commandeProduit.getQuantite(), dateSortie, produit);
                    ss.saveSortie(sortie);
                }
            } else if (status == 0) {
                Timestamp dateSortie = Timestamp.valueOf(commandeProduit.getCommande().getDateCommande().toString());
                Sortie sortie = ss.getSortieByProduitAndQuantiteAndDateSortie(produit, commandeProduit.getQuantite(),
                        dateSortie);
                ss.deleteSortie(sortie);
            }

            cos.saveCommandeProduit(commandeProduit);

            obj.add(p);
            obj.add(commandeProduit);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list-admin")
    public ResponseEntity<APIResponse> getAllCommandeAdmin(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "10") int nbrParPage, @RequestParam(defaultValue = "1") int noPage) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            List<Object> obj = new ArrayList<>();
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // System.out.println("id Utilisateur : " + idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);
            List<Commande> commandes = new ArrayList<>();
            int totalPages = 0;

            commandes = cos.listCommandeByAdmin(nbrParPage, noPage);
            totalPages = (int) Math.ceil((double) cos.countCommandeMobile() / nbrParPage);

            obj.add(commandes);
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

    @PutMapping("/update_status-admin/{id_commande}")
    public ResponseEntity<APIResponse> updateStatusAdmin(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable(name = "id_commande") int idCommande,
            @RequestParam(defaultValue = "10") int nbrParPage, @RequestParam(defaultValue = "1") int noPage,
            @RequestParam int etat) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //     String token = authorizationHeader.substring(7);
            //     Claims claims = jwtUtil.parseJwtClaims(token);
            //     idUtilisateur = JwtUtil.getUserId(claims);
            // }

            // List<Object> obj = new ArrayList<>();
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            Commande commande = cos.getCommandeById(idCommande);
            commande.setStatus(etat);
            cos.saveCommande(commande);

            APIResponse api = new APIResponse(null, null);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
