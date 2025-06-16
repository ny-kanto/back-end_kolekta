package com.marche.marche.controller;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.marche.marche.api.APIResponse;
import com.marche.marche.authentification.JwtUtil;
import com.marche.marche.modele.Categorie;
import com.marche.marche.modele.Commentaire;
import com.marche.marche.modele.EtatStock;
import com.marche.marche.modele.Evaluation;
import com.marche.marche.modele.Personne;
import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPhotos;
import com.marche.marche.modele.Region;
import com.marche.marche.modele.TypeProduit;
import com.marche.marche.modele.Unite;
import com.marche.marche.modele.Utilisateur;
import com.marche.marche.services.CategorieService;
import com.marche.marche.services.CommentaireService;
import com.marche.marche.services.EntreeService;
import com.marche.marche.services.EvaluationService;
import com.marche.marche.services.PersonneService;
import com.marche.marche.services.ProduitPhotosService;
import com.marche.marche.services.ProduitService;
import com.marche.marche.services.RegionService;
import com.marche.marche.services.TypeProduitService;
import com.marche.marche.services.UniteService;
import com.marche.marche.services.UtilisateurService;
import com.marche.marche.utils.Utils;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

@RestController
@RequestMapping("/produit")
@CrossOrigin(origins = "*")
public class ProduitController {
    @Autowired
    private ProduitService ps;

    @Autowired
    private EvaluationService evs;

    @Autowired
    private CommentaireService cos;

    @Autowired
    private TypeProduitService tps;

    @Autowired
    private ProduitPhotosService pps;

    @Autowired
    private PersonneService pes;

    @Autowired
    private UtilisateurService us;

    @Autowired
    private CategorieService cs;

    @Autowired
    private RegionService rs;

    @Autowired
    private UniteService uns;

    @Autowired
    private EntreeService es;

    @Autowired
    private CommentaireService coms;

    @Autowired
    private JwtUtil jwtUtil;

    // CONTROLLEUR D'AJOUT DE PRODUIT DU VENDEUR
    @PostMapping("/save")
    public ResponseEntity<APIResponse> saveProduct(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam String nom, @RequestParam String description,
            @RequestParam String prix, @RequestParam("min_commande") String minCommande,
            @RequestParam("delais_livraison") String delaisLivraison, @RequestParam("id_categorie") String idCategorie,
            @RequestParam("id_unite") String idUnite, @RequestParam String localisation,
            @RequestParam("id_region") String idRegion, @RequestParam(required = false) MultipartFile[] photo) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }

            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);
            Categorie categorie = cs.getCategorieById(Integer.valueOf(idCategorie));
            Unite unite = uns.getUniteById(Integer.valueOf(idUnite));
            Region region = rs.getRegionById(Integer.valueOf(idRegion));

            Timestamp dateAjout = Timestamp.from(Instant.now());

            Produit produit = new Produit(nom, description, Double.valueOf(prix), Double.valueOf(minCommande),
                    Integer.valueOf(delaisLivraison), categorie, p, unite, dateAjout, localisation, region);

            ps.saveProduit(produit);

            List<Object> obj = new ArrayList<>();

            if (photo != null && photo.length > 0) {
                for (MultipartFile file : photo) {
                    byte[] photoBytes = file.getBytes();

                    ProduitPhotos produitPhotos = new ProduitPhotos();
                    produitPhotos.setPhotos(photoBytes);
                    produitPhotos.setProduit(produit);

                    pps.saveProduitPhotos(produitPhotos);

                    obj.add(produitPhotos);
                }
            }

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (MaxUploadSizeExceededException e) {
            e.printStackTrace();
            APIResponse response = new APIResponse("La taille des fichiers dépasse la limite autorisée (500 KB)", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR DE MODIFICATION DE PRODUIT
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> update(@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable int id, @RequestBody Produit produit) {
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
            Categorie categorie = cs.getCategorieById(produit.getCategorie().getId());
            Unite unite = uns.getUniteById(produit.getUnite().getId());
            Region region = rs.getRegionById(produit.getRegion().getId());

            produit.setPersonne(p);
            produit.setCategorie(categorie);
            produit.setUnite(unite);
            produit.setRegion(region);

            Produit te = ps.updateProduit(id, produit, p);

            // obj.add(te);
            APIResponse api = new APIResponse(null, te);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR POUR VOIR LA LISTE DES PRODUITS DU VENDEUR
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAll(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "10") int nbrParPage,
            @RequestParam(defaultValue = "1") int noPage,
            @RequestParam(name = "filtre_nom", defaultValue = "") String nom,
            @RequestParam(name = "filtre_prix_min", defaultValue = "0.0") double prixMin,
            @RequestParam(name = "filtre_prix_max", defaultValue = "0.0") double prixMax,
            @RequestParam(name = "filtre_unite", defaultValue = "0") int idUnite,
            @RequestParam(name = "filtre_categorie", defaultValue = "0") int idCategorie,
            @RequestParam(name = "filtre_type_produit", defaultValue = "0") int idTypeProduit,
            @RequestParam(defaultValue = "ID_PRODUIT") String column,
            @RequestParam(defaultValue = "1") int sort) {
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
            List<Produit> produit = new ArrayList<>();
            List<Categorie> categorie = cs.getAll();
            List<Unite> unite = uns.getAll();
            List<TypeProduit> typeProduit = tps.getAll();
            List<Region> region = rs.getAll();
            int totalPages = 0;
            if (nom.equals("") && prixMin == 0.0 && prixMax == 0.0 && idUnite == 0 && idCategorie == 0
                    && idTypeProduit == 0) {
                produit = ps.getProduitPerPage(p.getId(), noPage, nbrParPage, column, sort);
                totalPages = (int) Math.ceil((double) ps.countProduit(p) / nbrParPage);
            } else {
                produit = ps.getProduitFiltre(p.getId(), nom, prixMin, prixMax, idUnite, idCategorie, idTypeProduit,
                        noPage, nbrParPage, column, sort);
                totalPages = (int) Math.ceil((double) ps.countProduitFiltre(p.getId(), nom, prixMin, prixMax, idUnite,
                        idCategorie, idTypeProduit) / nbrParPage);
            }
            obj.add(produit);
            obj.add(totalPages);
            obj.add(categorie);
            obj.add(unite);
            obj.add(typeProduit);
            obj.add(noPage);
            obj.add(sort);
            obj.add(column);
            obj.add(region);
            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR POUR VOIR LA LISTE DE TOUS LES PRODUITS DU VENDEUR
    @GetMapping("/all-product")
    public ResponseEntity<APIResponse> getAllProductSeller(
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
            List<Produit> produit = ps.getAllProduitsByPersonne(p);
            obj.add(produit);
            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> findById(@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable int id, @RequestParam(defaultValue = "10") int nbrParPage,
            @RequestParam(defaultValue = "1") int noPage) {
        try {
            // int idUtilisateur = 0;
            // if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            // {
            // String token = authorizationHeader.substring(7);
            // Claims claims = jwtUtil.parseJwtClaims(token);
            // idUtilisateur = JwtUtil.getUserId(claims);
            // }
            // Utilisateur u = us.getUtilisateur(idUtilisateur);
            // Personne p = pes.getPersonneByUtilisateur(u);

            List<Object> obj = new ArrayList<>();
            Produit te = ps.getProduit(id);
            List<ProduitPhotos> pp = pps.getProduitPhotosByProduit(te);

            List<Commentaire> commentaires = coms.getCommentaireByProduit(te, nbrParPage, noPage);

            int totalPages = (int) Math.ceil((double) cos.countCommentaire(te) / nbrParPage);

            List<Map<String, String>> photosWithMime = new ArrayList<>();
            if (pp != null) {
                if (pp.size() > 0) {
                    for (ProduitPhotos photo : pp) {
                        String mimeType = Utils.guessMimeType(photo.getPhotos());
                        Map<String, String> photoData = new HashMap<>();
                        photoData.put("base64", Base64.getEncoder().encodeToString(photo.getPhotos()));
                        photoData.put("mimeType", mimeType);
                        photosWithMime.add(photoData);
                    }
                }
            }

            List<Evaluation> evaluations = evs.getEvaluationByProduit(te);

            Map<Integer, Long> evaluationCountNote = evs.getEvaluationCountsByNote(te);
            Map<Integer, Double> percentageByNote = new HashMap<>();

            int totalCount = 0;
            double weightedSum = 0.0;

            int note = 0;
            double percentage = 0.0;

            for (long count : evaluationCountNote.values()) {
                totalCount += count;
            }

            long count = 0;

            for (Map.Entry<Integer, Long> entry : evaluationCountNote.entrySet()) {
                note = entry.getKey();
                count = entry.getValue();

                weightedSum += note * count;
                percentage = totalCount > 0 ? (count * 100.0) / totalCount : 0.0;

                percentageByNote.put(note, percentage);
            }

            double averageRating = totalCount > 0 ? weightedSum / totalCount : 0;

            EtatStock etatStock = es.getEtatStockByIdProduit(id);

            obj.add(te);
            obj.add(photosWithMime);
            obj.add(etatStock.getReste());
            obj.add(commentaires);
            obj.add(totalPages);
            obj.add(noPage);
            obj.add(evaluations);
            obj.add(evaluationCountNote);
            obj.add(percentageByNote);
            obj.add(totalCount);
            obj.add(averageRating);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // CONTROLLEUR POUR SUPPRIMER UN PRODUIT DU VENDEUR
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> delete(@RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable int id) {
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

            ps.deleteProduit(id, p);

            obj.add(ps);
            obj.add(p);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user-all")
    public ResponseEntity<APIResponse> getAllProduct(@RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "20") int nbrParPage,
            @RequestParam(defaultValue = "1") int noPage,
            @RequestParam(defaultValue = "") String nom,
            @RequestParam(name = "localisation", defaultValue = "0") int idLocalisation,
            @RequestParam(name = "type_production", defaultValue = "0") int idTypeProduction,
            @RequestParam(defaultValue = "0") int disponibilite,
            @RequestParam(name = "prix_min", defaultValue = "0.0") double prixMin,
            @RequestParam(name = "prix_max", defaultValue = "0.0") double prixMax,
            @RequestParam(name = "categorie", defaultValue = "0") int idCategorie,
            @RequestParam(name = "type_produit", defaultValue = "0") int idTypeProduit,
            @RequestParam(defaultValue = "id_produit") String column,
            @RequestParam(defaultValue = "1") int sort) {
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
            // Personne p = pes.getPersonneByUtilisateur(u);
            List<Produit> produits = new ArrayList<>();
            List<Categorie> categories = cs.getAll();
            List<Unite> unites = uns.getAll();
            List<TypeProduit> typeProduits = tps.getAll();
            List<Region> regions = rs.getAll();
            int totalPages = 0;

            if (nom.equals("") && idLocalisation == 0 && idTypeProduction == 0 && disponibilite == 0
                    && prixMin == 0.0 && prixMax == 0.0 && idCategorie == 0 && idTypeProduit == 0) {
                produits = ps.getProduitUserPerPage(noPage, nbrParPage, column, sort);
                totalPages = (int) Math.ceil((double) ps.countProduit() / nbrParPage);
            } else {
                produits = ps.getProduitUserFiltre(nom, idLocalisation, idTypeProduction, disponibilite, prixMin,
                        prixMax,
                        idCategorie, idTypeProduit, noPage, nbrParPage, column, sort);
                totalPages = (int) Math.ceil((double) ps.countProduitFiltreUser(nom, idLocalisation, idTypeProduction,
                        disponibilite, prixMin, prixMax,
                        idCategorie, idTypeProduit) / nbrParPage);
            }

            Map<String, String> photoData = null;
            ProduitPhotos photos = null;
            String mimeType = "";
            HashMap<Integer, Map<String, String>> produitData = new HashMap<>();
            Map<Integer, Long> evaluationCountNote = null;
            int totalCount = 0;
            double weightedSum = 0.0;
            double averageRating = 0.0;
            int note = 0;
            long count = 0;
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime oneWeekAgo = today.minusWeeks(1);
            boolean isNew = false;

            double priceMin = ps.produitPrixMin();
            double priceMax = ps.produitPrixMax();

            for (Produit produit : produits) {
                photos = pps.getOneProduitPhotosByProduit(produit);
                if (photos != null) {
                    mimeType = Utils.guessMimeType(photos.getPhotos());
                    photoData = new HashMap<>();
                    photoData.put("base64", Base64.getEncoder().encodeToString(photos.getPhotos()));
                    photoData.put("mimeType", mimeType);
                    produitData.put(produit.getId(), photoData);
                }

                isNew = produit.getDateAjout().toLocalDateTime().isAfter(oneWeekAgo);

                evaluationCountNote = evs.getEvaluationCountsByNote(produit);
                totalCount = 0;
                weightedSum = 0;

                for (Map.Entry<Integer, Long> entry : evaluationCountNote.entrySet()) {
                    note = entry.getKey();
                    count = entry.getValue();

                    totalCount += count;
                    weightedSum += note * count;
                }

                averageRating = totalCount > 0 ? weightedSum / totalCount : 0;
                produit.setAverageRating(averageRating);
                produit.setTotalCount(totalCount);
                produit.setNew(isNew);
            }

            obj.add(produits);
            obj.add(produitData);
            obj.add(totalPages);
            obj.add(categories);
            obj.add(unites);
            obj.add(typeProduits);
            obj.add(noPage);
            obj.add(sort);
            obj.add(column);
            obj.add(priceMin);
            obj.add(priceMax);
            obj.add(regions);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<APIResponse> findByIdForUser(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable int id, @RequestParam(defaultValue = "10") int nbrParPage,
            @RequestParam(defaultValue = "1") int noPage) {
        try {
            int idUtilisateur = 0;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                Claims claims = jwtUtil.parseJwtClaims(token);
                idUtilisateur = JwtUtil.getUserId(claims);
            }
            Utilisateur u = us.getUtilisateur(idUtilisateur);
            Personne p = pes.getPersonneByUtilisateur(u);

            List<Object> obj = new ArrayList<>();
            Produit te = ps.getProduit(id);
            List<ProduitPhotos> pp = pps.getProduitPhotosByProduit(te);

            List<Map<String, String>> photosWithMime = new ArrayList<>();
            if (pp != null) {
                if (pp.size() > 0) {
                    for (ProduitPhotos photo : pp) {
                        String mimeType = Utils.guessMimeType(photo.getPhotos());
                        Map<String, String> photoData = new HashMap<>();
                        photoData.put("base64", Base64.getEncoder().encodeToString(photo.getPhotos()));
                        photoData.put("mimeType", mimeType);
                        photosWithMime.add(photoData);
                    }
                }
            }

            Evaluation evaluation = evs.getEvaluationByProduitAndPersonne(te, p);

            if (evaluation == null) {
                evs.saveEvaluation(new Evaluation(p, te, 0));
            }

            List<Evaluation> evaluations = evs.getEvaluationByProduit(te);
            List<Commentaire> commentaires = cos.getCommentaireByProduit(te, nbrParPage, noPage);
            int totalPages = (int) Math.ceil((double) cos.countCommentaire(te) / nbrParPage);

            Map<Integer, Long> evaluationCountNote = evs.getEvaluationCountsByNote(te);
            Map<Integer, Double> percentageByNote = new HashMap<>();

            int totalCount = 0;
            double weightedSum = 0.0;

            int note = 0;
            double percentage = 0.0;

            for (long count : evaluationCountNote.values()) {
                totalCount += count;
            }

            long count = 0;

            for (Map.Entry<Integer, Long> entry : evaluationCountNote.entrySet()) {
                note = entry.getKey();
                count = entry.getValue();

                weightedSum += note * count;
                percentage = totalCount > 0 ? (count * 100.0) / totalCount : 0.0;

                percentageByNote.put(note, percentage);
            }

            double averageRating = totalCount > 0 ? weightedSum / totalCount : 0;

            EtatStock etatStock = es.getEtatStockByIdProduit(id);

            obj.add(te);
            obj.add(photosWithMime);
            obj.add(etatStock.getReste());
            obj.add(evaluation);
            obj.add(evaluations);
            obj.add(commentaires);
            obj.add(evaluationCountNote);
            obj.add(percentageByNote);
            obj.add(totalCount);
            obj.add(averageRating);
            obj.add(totalPages);
            obj.add(noPage);

            APIResponse api = new APIResponse(null, obj);
            return ResponseEntity.ok(api);
        } catch (Exception e) {
            e.printStackTrace();
            APIResponse response = new APIResponse(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}