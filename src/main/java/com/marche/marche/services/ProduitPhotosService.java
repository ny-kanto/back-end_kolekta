package com.marche.marche.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Produit;
import com.marche.marche.modele.ProduitPhotos;
import com.marche.marche.repository.ProduitPhotosRepository;

@Service
public class ProduitPhotosService {

    @Autowired
    private ProduitPhotosRepository produitPhotosRepository;

    public ProduitPhotos saveProduitPhotos(ProduitPhotos produitPhotos) {
        return produitPhotosRepository.save(produitPhotos);
    }

    public List<ProduitPhotos> getProduitPhotosByProduit(Produit produit) {
        return produitPhotosRepository.findByProduit(produit);
    }

    public ProduitPhotos getOneProduitPhotosByProduit(Produit produit) {
        List<ProduitPhotos> photos = produitPhotosRepository.findByProduit(produit);

        if (photos != null && !photos.isEmpty()) {
            return photos.get(0);
        }

        return null;
    }
}
