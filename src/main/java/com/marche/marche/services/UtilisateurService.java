package com.marche.marche.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Utilisateur;
import com.marche.marche.repository.UtilisateurRepository;

@Service
public class UtilisateurService {
    @Autowired
    private UtilisateurRepository ur;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUtilisateur(Utilisateur user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        ur.save(user);
    }

    public Utilisateur getUtilisateur(int id) {
        return ur.findById(id).get();
    }
}
