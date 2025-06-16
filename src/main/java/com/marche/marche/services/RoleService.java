package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Role;
import com.marche.marche.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository rr;

    public Role getRoleById(int id) {
        return rr.findById(id).get();
    }
}
