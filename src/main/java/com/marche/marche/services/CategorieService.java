package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Categorie;
import com.marche.marche.repository.CategorieRepository;

import java.util.List;

@Service
public class CategorieService {
    @Autowired
    private CategorieRepository cr;

    public Categorie getCategorieById(int id) {
        return cr.findById(id).get();
    }

    public List<Categorie> getAll() {
        return cr.findAll();
    }

    public void saveCategorie(Categorie categorie) {
        cr.save(categorie);
    }
}
