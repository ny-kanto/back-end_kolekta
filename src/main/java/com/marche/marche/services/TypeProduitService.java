package com.marche.marche.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.TypeProduit;
import com.marche.marche.repository.TypeProduitRepository;

@Service
public class TypeProduitService {
    @Autowired
    private TypeProduitRepository tpr;

    public TypeProduit getTypeProduitById(int id) {
        return tpr.findById(id).get();
    }

    public List<TypeProduit> getAll() {
        return tpr.findAll();
    }
}
