package com.marche.marche.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.TypeProduction;
import com.marche.marche.repository.TypeProductionRepository;

@Service
public class TypeProductionService {
    @Autowired
    private TypeProductionRepository tpr;

    public TypeProduction getTypeProductionById(int id) {
        return tpr.findById(id).get();
    }

    public List<TypeProduction> getAll() {
        return tpr.findAll();
    }
}
