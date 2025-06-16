package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Region;
import com.marche.marche.repository.RegionRepository;

import java.util.List;

@Service
public class RegionService {
    @Autowired
    private RegionRepository rr;

    public Region getRegionById(int id) {
        return rr.findById(id).get();
    }

    public List<Region> getAll() {
        return rr.findAll();
    }
}
