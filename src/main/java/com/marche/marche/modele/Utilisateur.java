package com.marche.marche.modele;

import java.sql.Date;

import com.marche.marche.repository.PersonneRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
@SequenceGenerator(name = "utilisateur_seq", sequenceName = "utilisateur_seq", allocationSize = 1)
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "utilisateur_seq")
    private int id;

    @Column(unique = true)
    private String pseudo;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "date_inscription")
    private Date dateInscription;

    @Column(name = "is_admin")
    private int isAdmin;

    public Utilisateur(String pseudo, String email, String password, Date dateInscription) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.dateInscription = dateInscription;
        isAdmin = 0;
    }

    public Utilisateur(String pseudo, String email, String password, Date dateInscription, int isAdmin) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.dateInscription = dateInscription;
        this.isAdmin = isAdmin;
    }

    public Personne getPersonne(PersonneRepository pr) {
        return pr.findByUtilisateur(this);
    }

    public String getAdmin(PersonneRepository pr) {
        if (this.isAdmin == 1) {
            return "ADMIN";
        } else if (this.isAdmin == 0) {
            if (this.getPersonne(pr) != null) {
                if (this.getPersonne(pr).getRole().getId() == 1) {
                    return "USER_ACHETEUR";
                } else if (this.getPersonne(pr).getRole().getId() == 2) {
                    return "USER_VENDEUR";
                }
            }
        }
        return "";
    }

    public void setAdmin(String isAdmin) {
        if (isAdmin.equalsIgnoreCase("ROLE_ADMIN")) {
            this.isAdmin = 1;
        } else {
            this.isAdmin = 0;
        }
    }
}
