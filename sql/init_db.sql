-- ============================================
--  init_db.sql
--  Projet : Gestion Club Sportif
--  Auteur : Zakaria
--  ENSAO GI3 — 2025/2026
-- ============================================

CREATE DATABASE IF NOT EXISTS club_sportif CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE club_sportif;

-- Table 1 : Membres
CREATE TABLE IF NOT EXISTS membres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telephone VARCHAR(15) NOT NULL,
    dateNaissance DATE NOT NULL,
    actif TINYINT(1) NOT NULL DEFAULT 1
);

-- Table 2 : Abonnements
CREATE TABLE IF NOT EXISTS abonnements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    typeOffre VARCHAR(30) NOT NULL,
    prixMensuel DECIMAL(8, 2) NOT NULL,
    dureeEngagement INT NOT NULL,
    dateDebut DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
    membreId INT NOT NULL,
    FOREIGN KEY (membreId) REFERENCES membres (id) ON DELETE CASCADE
);

-- Données de test
INSERT INTO
    membres (
        nom,
        prenom,
        email,
        telephone,
        dateNaissance,
        actif
    )
VALUES (
        'El Amrani',
        'Youssef',
        'youssef@email.com',
        '0612345678',
        '1998-04-15',
        1
    ),
    (
        'Benali',
        'Sara',
        'sara@email.com',
        '0698765432',
        '2001-09-22',
        1
    ),
    (
        'Idrissi',
        'Karim',
        'karim@email.com',
        '0655443322',
        '1995-11-03',
        0
    );

INSERT INTO
    abonnements (
        typeOffre,
        prixMensuel,
        dureeEngagement,
        dateDebut,
        statut,
        membreId
    )
VALUES (
        'CLASSIQUE',
        290.00,
        12,
        '2025-01-01',
        'ACTIF',
        1
    ),
    (
        'JEUNE',
        250.00,
        12,
        '2025-03-01',
        'ACTIF',
        2
    ),
    (
        'SANS_ENGAGEMENT',
        340.00,
        1,
        '2024-06-01',
        'EXPIRE',
        3
    );