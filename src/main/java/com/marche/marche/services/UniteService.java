package com.marche.marche.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Unite;
import com.marche.marche.repository.UniteRepository;

@Service
public class UniteService {
    @Autowired
    private UniteRepository ur;

    public Unite getUniteById(int id) {
        return ur.findById(id).get();
    }

    public List<Unite> getAll() {
        return ur.findAll();
    }

    public void saveUnite(Unite unite) {
        ur.save(unite);
    }
}
