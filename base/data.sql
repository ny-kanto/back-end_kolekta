-- UTILISATEUR
-- INSERT INTO utilisateur (ID, email, password, is_admin) VALUES
-- (NEXTVAL('utilisateur_seq'), 'john.doe@example.com', 'password123', 0),
-- (NEXTVAL('utilisateur_seq'), 'jane.smith@example.com', 'securePass!456', 0),
-- (NEXTVAL('utilisateur_seq'), 'admin@example.com', 'adminSecret789', 1),
-- (NEXTVAL('utilisateur_seq'), 'alice.williams@example.com', 'alicePass123', 0),
-- (NEXTVAL('utilisateur_seq'), 'bob.johnson@example.com', 'bobSecure456', 0),
-- (NEXTVAL('utilisateur_seq'), 'charlie.brown@example.com', 'charliePass789', 0),
-- (NEXTVAL('utilisateur_seq'), 'daniel.wilson@example.com', 'danielSecret!123', 0),
-- (NEXTVAL('utilisateur_seq'), 'emily.davis@example.com', 'emilySecure456', 0),
-- (NEXTVAL('utilisateur_seq'), 'frank.miller@example.com', 'frankPass789', 0),
-- (NEXTVAL('utilisateur_seq'), 'george.moore@example.com', 'georgeSecret!123', 0);

-- ROLE
INSERT INTO ROLE (ID, NOM) VALUES
(NEXTVAL('role_seq'), 'Acheteur'),
(NEXTVAL('role_seq'), 'Vendeur');

-- UNITE
INSERT INTO UNITE (ID, NOM) VALUES 
(NEXTVAL('unite_seq'), 'Kilogramme'),
(NEXTVAL('unite_seq'), 'Litre'),
(NEXTVAL('unite_seq'), 'Pièce'),
(NEXTVAL('unite_seq'), 'Mètre'),
(NEXTVAL('unite_seq'), 'Gramme'),
(NEXTVAL('unite_seq'), 'Centimètre'),
(NEXTVAL('unite_seq'), 'Millilitre'),
(NEXTVAL('unite_seq'), 'Tonne'),
(NEXTVAL('unite_seq'), 'Pack'),
(NEXTVAL('unite_seq'), 'Boîte');

-- TYPE PRODUCTION
INSERT INTO TYPE_PRODUCTION (ID, NOM) VALUES
(NEXTVAL('type_production_seq'), 'biologique'),
(NEXTVAL('type_production_seq'), 'durable');

-- TYPE PRODUIT
INSERT INTO TYPE_PRODUIT (ID, NOM) VALUES
(NEXTVAL('type_produit_seq'), 'Produits agricoles'),
(NEXTVAL('type_produit_seq'), 'Produits d''élevages');

-- REGION
INSERT INTO REGION (ID, NOM) VALUES
(NEXTVAL('region_seq'), 'Analamanga'),
(NEXTVAL('region_seq'), 'Bongolava'),
(NEXTVAL('region_seq'), 'Itasy'),
(NEXTVAL('region_seq'), 'Vakinankaratra'),
(NEXTVAL('region_seq'), 'Diana'),
(NEXTVAL('region_seq'), 'Sava'),
(NEXTVAL('region_seq'), 'Amoron''i Mania'),
(NEXTVAL('region_seq'), 'Atsimo Atsinanana'),
(NEXTVAL('region_seq'), 'Fitovinany'),
(NEXTVAL('region_seq'), 'Haute Matsiatra'),
(NEXTVAL('region_seq'), 'Ihorombe'),
(NEXTVAL('region_seq'), 'Vatovavy'),
(NEXTVAL('region_seq'), 'Betsiboka'),
(NEXTVAL('region_seq'), 'Boeny'),
(NEXTVAL('region_seq'), 'Melaky'),
(NEXTVAL('region_seq'), 'Sofia'),
(NEXTVAL('region_seq'), 'Alaotra Mangoro'),
(NEXTVAL('region_seq'), 'Analanjirofo'),
(NEXTVAL('region_seq'), 'Atsinanana'),
(NEXTVAL('region_seq'), 'Androy'),
(NEXTVAL('region_seq'), 'Anosy'),
(NEXTVAL('region_seq'), 'Atsimo Andrefana'),
(NEXTVAL('region_seq'), 'Menabe');

-- CATEGORIE
INSERT INTO CATEGORIE (ID, NOM, id_type_produit) VALUES
(NEXTVAL('categorie_seq'), 'Fruits et Légumes', 1),
(NEXTVAL('categorie_seq'), 'Céréales et Légumineuses', 1),
(NEXTVAL('categorie_seq'), 'Plantes et Herbes Aromatiques', 1),
(NEXTVAL('categorie_seq'), 'Noix et Graines', 1),

(NEXTVAL('categorie_seq'), 'Viande', 2),
(NEXTVAL('categorie_seq'), 'Produits Laitiers', 2),
(NEXTVAL('categorie_seq'), 'Œufs', 2),
(NEXTVAL('categorie_seq'), 'Poissons et Fruits de Mer', 2),
(NEXTVAL('categorie_seq'), 'Miel et Produits de la Ruchee', 2);



-- PRODUIT
-- FRUITS ET LEGUMES
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'), 'Pommes', 'Description des pommes', 2.5, 1, 3, 1, 1, 1), -- Kilogramme
(NEXTVAL('produit_seq'),'Oranges', 'Description des oranges', 3.0, 1, 3, 1, 1, 1), -- Kilogramme
(NEXTVAL('produit_seq'),'Tomates', 'Description des tomates', 1.8, 1, 3, 1, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Carottes', 'Description des carottes', 1.2, 1, 3, 1, 1, 1), -- Kilogramme
(NEXTVAL('produit_seq'),'Salades', 'Description des salades', 1.5, 1, 3, 1, 3, 1); -- Pièce

-- CEREALES ET LEGUMINEUSES
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Blé', 'Description du blé', 1.0, 5, 7, 2, 8, 1), -- Tonne
(NEXTVAL('produit_seq'),'Maïs', 'Description du maïs', 1.2, 5, 7, 2, 8, 1), -- Tonne
(NEXTVAL('produit_seq'),'Riz', 'Description du riz', 2.0, 5, 7, 2, 8, 1), -- Tonne
-- (NEXTVAL('produit_seq'),'Lentilles', 'Description des lentilles', 2.5, 5, 7, 2, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Pois chiches', 'Description des pois chiches', 2.8, 5, 7, 2, 5, 1); -- Gramme

-- PLANTES ET HERBES AROMATIQUES
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Basilic', 'Description du basilic', 0.5, 1, 2, 3, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Menthe', 'Description de la menthe', 0.5, 1, 2, 3, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Persil', 'Description du persil', 0.5, 1, 2, 3, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Thym', 'Description du thym', 0.5, 1, 2, 3, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Lavande', 'Description de la lavande', 0.7, 1, 2, 3, 3, 1); -- Pièce

-- NOIX ET GRAINES
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Amandes', 'Description des amandes', 10.0, 1, 5, 4, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Noix', 'Description des noix', 9.0, 1, 5, 4, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Graines de tournesol', 'Description des graines de tournesol', 5.0, 1, 5, 4, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Graines de lin', 'Description des graines de lin', 6.0, 1, 5, 4, 5, 1); -- Gramme

-- Viande
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Bœuf', 'Description du bœuf', 20.0, 1, 2, 5, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Porc', 'Description du porc', 15.0, 1, 2, 5, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Poulet', 'Description du poulet', 12.0, 1, 2, 5, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Agneau', 'Description de l''agneau', 25.0, 1, 2, 5, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Dinde', 'Description de la dinde', 18.0, 1, 2, 5, 5, 1); -- Gramme

-- PRODUITS LAITIERS
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Lait', 'Description du lait', 1.5, 1, 1, 6, 2, 1), -- Litre
(NEXTVAL('produit_seq'),'Fromage', 'Description du fromage', 5.0, 1, 1, 6, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Yaourt', 'Description du yaourt', 2.0, 1, 1, 6, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Beurre', 'Description du beurre', 3.0, 1, 1, 6, 3, 1), -- Pièce
(NEXTVAL('produit_seq'),'Crème', 'Description de la crème', 4.0, 1, 1, 6, 7, 1); -- Millilitre

-- OEUFS
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Œufs de poule', 'Description des œufs de poule', 0.5, 12, 1, 7, 9, 1), -- Pack
(NEXTVAL('produit_seq'),'Œufs de caille', 'Description des œufs de caille', 1.0, 24, 1, 7, 9, 1); -- Pack

-- POISSONS ET FRUITS DE MER
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Poissons d''eau douce', 'Description des poissons d''eau douce', 10.0, 1, 2, 8, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Poissons de mer', 'Description des poissons de mer', 15.0, 1, 2, 8, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Crevettes', 'Description des crevettes', 20.0, 1, 2, 8, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Coquillages', 'Description des coquillages', 18.0, 1, 2, 8, 5, 1); -- Gramme

-- MIEL ET PRODUITS DE LA RUCHE
INSERT INTO PRODUIT (ID, NOM, DESCRIPTION, PRIX, MIN_COMMANDE, DELAIS_LIVRAISON, ID_CATEGORIE, ID_UNITE, ID_PERSONNE) VALUES
(NEXTVAL('produit_seq'),'Miel', 'Description du miel', 8.0, 1, 3, 9, 2, 1), -- Litre
(NEXTVAL('produit_seq'),'Cire d''abeille', 'Description de la cire d''abeille', 5.0, 1, 3, 9, 5, 1), -- Gramme
(NEXTVAL('produit_seq'),'Propolis', 'Description de la propolis', 12.0, 1, 3, 9, 5, 1); -- Gramme

INSERT INTO COMMANDE (ID, ADRESSE_LIVRAISON, DATE_COMMANDE, NUM_CLIENT, ID_PERSONNE, REFERENCE, STATUS, METHODE_PAIEMENT) VALUES
(NEXTVAL('commande_seq'), 'Antananarivo', '2024-08-01', 'C2/01082024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Fianarantsoa', '2024-09-02', 'C2/02092024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Mahajanga', '2024-08-03', 'C2/03082024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Toamasina', '2024-09-04', 'C3/04092024', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Antsirabe', '2024-10-05', 'C2/05102024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Toliara', '2024-08-06', 'C2/06082024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Antananarivo', '2024-09-07', 'C3/07092024', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Anosy', '2024-11-08', 'C3/08112024', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Andoharanofotsy', '2024-10-09', 'C3/09102024', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Tanjombato', '2024-11-10', 'C2/10112024', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Antananarivo', '2023-01-15', 'C3/15012023', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Fianarantsoa', '2023-02-20', 'C2/20022023', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Mahajanga', '2023-03-25', 'C3/25032023', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Toamasina', '2023-04-05', 'C2/04052023', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Antsirabe', '2022-05-10', 'C3/10052022', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Toliara', '2022-06-22', 'C3/22062022', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Antananarivo', '2022-07-30', 'C2/30072022', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Fianarantsoa', '2021-08-18', 'C2/18082021', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Mahajanga', '2021-09-20', 'C3/20092021', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Toamasina', '2021-10-15', 'C2/15102021', 2, '', 0, 0),

(NEXTVAL('commande_seq'), 'Anosy', '2024-12-08', 'C3/08122024', 3, '', 0, 0),

(NEXTVAL('commande_seq'), 'Tanjombato', '2025-01-10', 'C2/10012025', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Andoharanofotsy', '2025-01-15', 'C3/15012025', 3, '', 0, 0),

(NEXTVAL('commande_seq'), 'Anosipatrana', '2025-02-14', 'C2/12022025', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Ankaditapaka', '2025-02-19', 'C2/19022025', 2, '', 0, 0),

(NEXTVAL('commande_seq'), 'Anosy', '2025-03-05', 'C2/05032025', 2, '', 0, 0),
(NEXTVAL('commande_seq'), 'Andoharanofotsy', '2025-03-27', 'C3/27032025', 3, '', 0, 0),

(NEXTVAL('commande_seq'), 'Andoharanofotsy', '2025-04-20', 'C3/20042025', 3, '', 0, 0),
(NEXTVAL('commande_seq'), 'Andoharanofotsy', '2025-04-27', 'C3/27042025', 3, '', 0, 0),

(NEXTVAL('commande_seq'), 'Anosy', '2025-05-15', 'C2/15052025', 2, '', 0, 0);

INSERT INTO COMMANDE_PRODUIT (ID_COMMANDE, ID_PRODUIT, PRIX_UNITAIRE, QUANTITE, STATUS_COMMANDE) VALUES
(1, 1, 4500, 10, 11),
(1, 3, 3500, 20, 11),

(2, 5, 4000, 15, 1),
(2, 8, 5000, 30, 1),

(3, 11, 2000, 25, 11),
(3, 8, 4000, 5, 11),

(4, 17, 4000, 24, 0),
(4, 5 , 3500, 5, 0),
(4, 19, 9000, 5, 0),

(5, 27, 5000, 5, 11),
(5, 24, 1500, 50, 11),

(6, 27, 2500, 30, 11),
(6, 29, 6000, 20, 11),

(7, 32, 1800, 10, 11),
(7, 33, 5600, 8, 11),

(8, 3, 3500, 25, 1),

(9, 5, 4000, 10, 11),
(9, 17, 4000, 20, 11),

(10, 8, 5000, 40, 11),
(10, 33, 3000, 10, 11),

(11, 2, 2500, 12, 11),
(11, 5, 3000, 20, 11),

(12, 19, 4000, 8, 1), 
(12, 11, 5000, 15, 1),

(13, 17, 2000, 5, 11),
(13, 19, 3000, 25, 11),

(14, 27, 2500, 20, 0),

(15, 21, 4000, 5, 11),
(15, 32, 6000, 3, 11),

(16, 8, 4000, 15, 11),
(16, 1, 6000, 3, 11),

(17, 11, 4000, 5, 11),
(17, 17, 6000, 3, 11),

(18, 24, 4000, 5, 1),
(18, 3, 6000, 3, 1),

(19, 33, 4000, 5, 11),
(19, 19, 6000, 3, 11),

(20, 32, 1800, 5, 0);

INSERT INTO ENTREE (ID, DATE_ENTREE, QUANTITE, ID_PRODUIT) VALUES
(NEXTVAL('entree_seq'), '2020-08-25', 100, 2),
(NEXTVAL('entree_seq'), '2020-08-25', 50, 3),
(NEXTVAL('entree_seq'), '2020-09-01', 150, 1),
(NEXTVAL('entree_seq'), '2020-09-02', 75, 4),
(NEXTVAL('entree_seq'), '2020-09-02', 200, 5),
(NEXTVAL('entree_seq'), '2020-09-03', 100, 8),
(NEXTVAL('entree_seq'), '2020-09-04', 90, 11),
(NEXTVAL('entree_seq'), '2020-09-05', 120, 17),
(NEXTVAL('entree_seq'), '2020-09-06', 50, 19),
(NEXTVAL('entree_seq'), '2020-09-06', 200, 24),
(NEXTVAL('entree_seq'), '2020-09-07', 180, 27),
(NEXTVAL('entree_seq'), '2020-09-08', 250, 29),
(NEXTVAL('entree_seq'), '2020-09-09', 100, 32),
(NEXTVAL('entree_seq'), '2020-09-09', 300, 3),
(NEXTVAL('entree_seq'), '2020-09-10', 400, 5),
(NEXTVAL('entree_seq'), '2020-09-10', 100, 17),
(NEXTVAL('entree_seq'), '2020-09-11', 200, 8),
(NEXTVAL('entree_seq'), '2020-09-11', 50, 19),
(NEXTVAL('entree_seq'), '2020-09-12', 100, 21),
(NEXTVAL('entree_seq'), '2020-09-12', 150, 33),
(NEXTVAL('entree_seq'),	'2024-01-10', 100, 6),
(NEXTVAL('entree_seq'),	'2024-01-10', 10000, 9),
(NEXTVAL('entree_seq'),	'2024-01-10', 500, 18),
(NEXTVAL('entree_seq'),	'2024-01-10', 20000, 20),
(NEXTVAL('entree_seq'),	'2024-01-10', 50, 26),
(NEXTVAL('entree_seq'),	'2024-01-10', 2000,	23),
(NEXTVAL('entree_seq'),	'2024-01-10', 50, 7),
(NEXTVAL('entree_seq'),	'2024-01-10', 1000, 34);

INSERT INTO SORTIE (ID, DATE_SORTIE, QUANTITE, ID_PRODUIT) VALUES
(NEXTVAL('sortie_seq'), '2024-09-01', 10, 1),
(NEXTVAL('sortie_seq'), '2024-09-01', 20, 3),
(NEXTVAL('sortie_seq'), '2024-09-02', 15, 5),
(NEXTVAL('sortie_seq'), '2024-09-02', 30, 8),
(NEXTVAL('sortie_seq'), '2024-09-03', 25, 11),
(NEXTVAL('sortie_seq'), '2024-09-03', 5, 8),
(NEXTVAL('sortie_seq'), '2024-09-05', 5, 27),
(NEXTVAL('sortie_seq'), '2024-09-05', 50, 24),
(NEXTVAL('sortie_seq'), '2024-09-06', 30, 27),
(NEXTVAL('sortie_seq'), '2024-09-06', 20, 29),
(NEXTVAL('sortie_seq'), '2024-09-07', 10, 32),
(NEXTVAL('sortie_seq'), '2024-09-07', 8, 33),
(NEXTVAL('sortie_seq'), '2024-09-08', 25, 3),
(NEXTVAL('sortie_seq'), '2024-09-09', 10, 5),
(NEXTVAL('sortie_seq'), '2024-09-09', 20, 17),
(NEXTVAL('sortie_seq'), '2024-09-10', 20, 8),
(NEXTVAL('sortie_seq'), '2024-09-10', 10, 33),
(NEXTVAL('sortie_seq'), '2023-01-15', 12, 2),
(NEXTVAL('sortie_seq'), '2023-01-15', 20, 5),
(NEXTVAL('sortie_seq'), '2023-02-20', 8, 19),
(NEXTVAL('sortie_seq'), '2023-02-20', 15, 11),
(NEXTVAL('sortie_seq'), '2023-03-25', 5, 17),
(NEXTVAL('sortie_seq'), '2023-03-25', 25, 19),
(NEXTVAL('sortie_seq'), '2022-05-10', 5, 21),
(NEXTVAL('sortie_seq'), '2022-05-10', 3, 32),
(NEXTVAL('sortie_seq'), '2022-06-22', 15, 8),
(NEXTVAL('sortie_seq'), '2022-06-22', 3, 1),
(NEXTVAL('sortie_seq'), '2022-07-30', 5, 11),
(NEXTVAL('sortie_seq'), '2022-07-30', 3, 17),
(NEXTVAL('sortie_seq'), '2021-08-18', 5, 24),
(NEXTVAL('sortie_seq'), '2021-08-18', 3, 3),
(NEXTVAL('sortie_seq'), '2021-09-20', 5, 33),
(NEXTVAL('sortie_seq'), '2021-09-20', 3, 19);


UPDATE PRODUIT
SET LOCALISATION = CASE
    WHEN ID = 1 THEN 'Antananarivo'
    WHEN ID = 2 THEN 'Antsirabe'
    WHEN ID = 3 THEN 'Miarinarivo'
    WHEN ID = 4 THEN 'Toamasina'
    WHEN ID = 5 THEN 'Fianarantsoa'
    WHEN ID = 6 THEN 'Tsiroanomandidy'
    WHEN ID = 7 THEN 'Ambatondrazaka'
    WHEN ID = 8 THEN 'Maevatanana'
    WHEN ID = 9 THEN 'Mahajanga'
    WHEN ID = 10 THEN 'Antsiranana'
    WHEN ID = 11 THEN 'Sambava'
    WHEN ID = 12 THEN 'Antsohihy'
    WHEN ID = 13 THEN 'Maintirano'
    WHEN ID = 14 THEN 'Morondava'
    WHEN ID = 15 THEN 'Fénérive Est'
    WHEN ID = 16 THEN 'Toliara'
    WHEN ID = 17 THEN 'Ambovombe'
    WHEN ID = 18 THEN 'Taolagnaro'
    WHEN ID = 19 THEN 'Mananjary'
    WHEN ID = 20 THEN 'Manakara'
    WHEN ID = 21 THEN 'Farafangana'
    WHEN ID = 22 THEN 'Ihosy'
    WHEN ID = 23 THEN 'Ambositra'
    WHEN ID = 24 THEN 'Betafo'
    WHEN ID = 25 THEN 'Antanifotsy'
    WHEN ID = 26 THEN 'Vatomandry'
    WHEN ID = 27 THEN 'Moramanga'
    WHEN ID = 28 THEN 'Ambanja'
    WHEN ID = 29 THEN 'Vohémar'
    WHEN ID = 30 THEN 'Belo sur Tsiribihina'
    WHEN ID = 31 THEN 'Morombe'
    WHEN ID = 32 THEN 'Beloha'
    WHEN ID = 33 THEN 'Ambohimahasoa'
    WHEN ID = 34 THEN 'Ifanadiana'
    WHEN ID = 35 THEN 'Vangaindrano'
    ELSE LOCALISATION
END,
id_region = CASE
    WHEN ID = 1 THEN 1
    WHEN ID = 2 THEN 2
    WHEN ID = 3 THEN 3
    WHEN ID = 4 THEN 4
    WHEN ID = 5 THEN 5
    WHEN ID = 6 THEN 6
    WHEN ID = 7 THEN 7
    WHEN ID = 8 THEN 8
    WHEN ID = 9 THEN 9
    WHEN ID = 10 THEN 10
    WHEN ID = 11 THEN 11
    WHEN ID = 12 THEN 12
    WHEN ID = 13 THEN 13
    WHEN ID = 14 THEN 14
    WHEN ID = 15 THEN 15
    WHEN ID = 16 THEN 16
    WHEN ID = 17 THEN 17
    WHEN ID = 18 THEN 18
    WHEN ID = 19 THEN 19
    WHEN ID = 20 THEN 20
    WHEN ID = 21 THEN 21
    WHEN ID = 22 THEN 22
    WHEN ID = 23 THEN 23
    WHEN ID = 24 THEN 2
    WHEN ID = 25 THEN 2
    WHEN ID = 26 THEN 4
    WHEN ID = 27 THEN 4
    WHEN ID = 28 THEN 10
    WHEN ID = 29 THEN 11
    WHEN ID = 30 THEN 13
    WHEN ID = 31 THEN 14
    WHEN ID = 32 THEN 17
    WHEN ID = 33 THEN 19
    WHEN ID = 34 THEN 19
    WHEN ID = 35 THEN 21
    ELSE id_region
END;

-- INSERT INTO CONVERSATION (ID, ID_VENDEUR, ID_ACHETEUR) VALUES
-- (NEXTVAL('conversation_seq'), 1, 2);

-- INSERT INTO MESSAGE (ID, ID_CONVERSATION, CONTENU_MESSAGE, DATE_MESSAGE, ID_EXPEDITEUR) VALUES
-- (NEXTVAL('message_seq'), 1, 'Bonjour, vos commandes vont être livrées d''ici 1 jours', '2024-10-05 10:30:02', 1),
-- (NEXTVAL('message_seq'), 1, 'Bonjour, merci pour votre service, je veux que la livraison de mes produits soient fait à Ampefiloha', '2024-10-05 10:35:00', 2);
