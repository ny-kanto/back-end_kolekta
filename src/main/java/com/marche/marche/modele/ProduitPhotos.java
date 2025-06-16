package com.marche.marche.modele;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produit_photos")
@SequenceGenerator(
    name = "produit_photos_seq", 
    sequenceName = "produit_photos_seq", 
    allocationSize = 1
)
public class ProduitPhotos {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produit_photos_seq")
    private int id;

    private byte[] photos;

    @ManyToOne
    @JoinColumn(name = "id_produit", referencedColumnName = "id")
    private Produit produit;
}
