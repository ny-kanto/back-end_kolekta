-- ETAT DE STOCK
CREATE OR REPLACE VIEW V_ETAT_STOCK AS
    SELECT
        P.ID                                                                            AS ID_PRODUIT,
        PE.ID                                                                           AS ID_PERSONNE,
        P.NOM                                                                           AS NOM_PRODUIT,
        U.NOM                                                                           AS NOM_UNITE,
        COALESCE(SUM(E.QTE), 0)                                                         AS SOMME_ENTREE,
        COALESCE(SUM(S.QTE), 0)                                                         AS SOMME_SORTIE,
        COALESCE(SUM(R.QTE), 0)                                                         AS SOMME_RESERVE,
        (COALESCE(SUM(E.QTE), 0) - (COALESCE(SUM(S.QTE), 0) + COALESCE(SUM(R.QTE), 0))) AS RESTE
    FROM
        PRODUIT  P
        LEFT JOIN (
            SELECT
                ID_PRODUIT,
                SUM(QUANTITE) AS QTE
            FROM
                ENTREE
            GROUP BY
                ID_PRODUIT
        ) E
        ON P.ID = E.ID_PRODUIT
        LEFT JOIN (
            SELECT
                ID_PRODUIT,
                SUM(QUANTITE) AS QTE
            FROM
                SORTIE
            GROUP BY
                ID_PRODUIT
        ) S
        ON P.ID = S.ID_PRODUIT
        LEFT JOIN (
            SELECT
                ID_PRODUIT,
                SUM(QUANTITE) AS QTE
            FROM
                COMMANDE_PRODUIT
            WHERE
                STATUS_COMMANDE = 0
            GROUP BY
                ID_PRODUIT
        ) R
        ON P.ID = R.ID_PRODUIT
        JOIN PERSONNE PE
        ON PE.ID = P.ID_PERSONNE
        JOIN UNITE U
        ON U.ID = P.ID_UNITE
    GROUP BY
        P.ID,
        PE.ID,
        P.NOM,
        U.NOM;

-- LISTE DES PRODUITS AVEC LEURS NOTES MOYENNES RESPECTIFS ET LES RESTES DANS LE STOCK
CREATE OR REPLACE VIEW V_PRODUIT_NOTE_STOCK AS
    SELECT
        P.ID                                AS ID_PRODUIT,
        P.NOM                               AS NOM_PRODUIT,
        P.PRIX                              AS PRIX_PRODUIT,
        P.DESCRIPTION                       AS DESCRIPTION_PRODUIT,
        P.DELAIS_LIVRAISON                  AS DELAIS_LIVRAISON_PRODUIT,
        P.MIN_COMMANDE                      AS MIN_COMMANDE_PRODUIT,
        P.DATE_AJOUT                        AS DATE_AJOUT_PRODUIT,
        P.ID_UNITE                          AS ID_UNITE,
        P.ID_CATEGORIE                      AS ID_CATEGORIE,
        P.LOCALISATION                      AS LOCALISATION_PRODUIT,
        P.ID_REGION                         AS ID_REGION,
        PE.ID                               AS ID_PERSONNE,
        COALESCE(AVG(NULLIF(E.NOTE, 0)), 0) AS NOTE_PRODUIT,
        ES.RESTE                            AS RESTE_STOCK
    FROM
        PRODUIT      P
        JOIN PERSONNE PE
        ON PE.ID = P.ID_PERSONNE
        LEFT JOIN EVALUATION E
        ON E.ID_PRODUIT = P.ID
        LEFT JOIN V_ETAT_STOCK ES
        ON P.ID = ES.ID_PRODUIT
    GROUP BY
        P.ID,
        P.NOM,
        P.PRIX,
        PE.ID,
        ES.RESTE;

-- TOTAL DE VENTE PAR CATEGORIE
CREATE OR REPLACE VIEW TOTAL_VENTES_PAR_CATEGORIE AS
    SELECT
        C.ID         AS ID_CATEGORIE,
        C.NOM        AS CATEGORIE_NOM,
        COALESCE(SUM(
            CASE
                WHEN CP.STATUS_COMMANDE >= 1 THEN
                    CP.QUANTITE * CP.PRIX_UNITAIRE
                ELSE
                    0
            END), 0) AS TOTAL_VENTES
    FROM
        CATEGORIE        C
        LEFT JOIN PRODUIT P
        ON C.ID = P.ID_CATEGORIE
        LEFT JOIN COMMANDE_PRODUIT CP
        ON P.ID = CP.ID_PRODUIT
        LEFT JOIN COMMANDE CO
        ON CP.ID_COMMANDE = CO.ID
    GROUP BY
        C.ID,
        C.NOM
    ORDER BY
        C.ID;

-- TOTAL DE VENTE PAR TYPE PRODUIT
CREATE OR REPLACE VIEW TOTAL_VENTES_PAR_TYPE_PRODUIT AS
    SELECT
        TP.ID        AS ID_TYPE_PRODUIT,
        TP.NOM       AS TYPE_PRODUIT_NOM,
        COALESCE(SUM(
            CASE
                WHEN CP.STATUS_COMMANDE >= 1 THEN
                    CP.QUANTITE * CP.PRIX_UNITAIRE
                ELSE
                    0
            END), 0) AS TOTAL_VENTES
    FROM
        TYPE_PRODUIT     TP
        LEFT JOIN CATEGORIE C
        ON TP.ID = C.ID_TYPE_PRODUIT
        LEFT JOIN PRODUIT P
        ON C.ID = P.ID_CATEGORIE
        LEFT JOIN COMMANDE_PRODUIT CP
        ON P.ID = CP.ID_PRODUIT
        LEFT JOIN COMMANDE C2
        ON CP.ID_COMMANDE = C2.ID
    GROUP BY
        TP.ID,
        TP.NOM
    ORDER BY
        TP.ID;

-- TOTAL DE VENTE PAR REGION
CREATE OR REPLACE VIEW TOTAL_VENTES_PAR_REGION AS
    SELECT
        R.ID         AS ID_REGION,
        R.NOM        AS REGION_NOM,
        COALESCE(SUM(
            CASE
                WHEN CP.STATUS_COMMANDE >= 1 THEN
                    CP.QUANTITE * CP.PRIX_UNITAIRE
                ELSE
                    0
            END), 0) AS TOTAL_VENTES
    FROM
        REGION           R
        LEFT JOIN PRODUIT P
        ON R.ID = P.ID_REGION
        LEFT JOIN COMMANDE_PRODUIT CP
        ON P.ID = CP.ID_PRODUIT
        LEFT JOIN COMMANDE C
        ON CP.ID_COMMANDE = C.ID
    GROUP BY
        R.ID,
        R.NOM
    ORDER BY
        R.ID;