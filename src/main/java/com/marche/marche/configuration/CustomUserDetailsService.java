package com.marche.marche.configuration;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.marche.marche.modele.Utilisateur;
import com.marche.marche.repository.PersonneRepository;
import com.marche.marche.repository.UtilisateurRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UtilisateurRepository dbUserRepository;

    @Autowired
    private PersonneRepository personneRepository;

    public CustomUserDetailsService(UtilisateurRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur user = dbUserRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Aucun utilisateur qui correspond à l'email");
        }
        System.out.println("email : " + user.getEmail());
        List<String> roles = new ArrayList<>();
        roles.add(user.getAdmin(personneRepository));
        UserDetails userDetails = User.builder().username(user.getEmail()).password(user.getPassword())
                .roles(roles.toArray(new String[0])).build();
        // System.out.println("role " + roles.get(0));
        return userDetails;
    }

    // private List<GrantedAuthority> getGrantedAuthorities(String role) {
    // List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    // authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    // return authorities;
    // }

}
