-- ============================================================
--   SCHÉMA BASE DE DONNÉES - AI Productivity Coach
--   Compatible MySQL 8+ et PostgreSQL 14+
-- ============================================================

CREATE DATABASE IF NOT EXISTS productivity_coach
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE productivity_coach;

-- -------------------------------------------------------
--  TABLE : utilisateurs
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS utilisateurs (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(100)        NOT NULL,
    prenom        VARCHAR(100)        NOT NULL,
    email         VARCHAR(150)        NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255)        NOT NULL,   -- BCrypt hash
    objectifs     TEXT,
    rythme        ENUM('matin','soir','flexible') DEFAULT 'flexible',
    cree_le       TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
    modifie_le    TIMESTAMP           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
--  TABLE : categories
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nom      VARCHAR(50)  NOT NULL,
    couleur  VARCHAR(7)   DEFAULT '#3498db',   -- code HEX
    icone    VARCHAR(50)
);

INSERT INTO categories (nom, couleur, icone) VALUES
    ('Études',    '#9b59b6', 'book'),
    ('Travail',   '#2ecc71', 'briefcase'),
    ('Personnel', '#e74c3c', 'user');

-- -------------------------------------------------------
--  TABLE : taches
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS taches (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id  INT             NOT NULL,
    categorie_id    INT             NOT NULL,
    titre           VARCHAR(200)    NOT NULL,
    description     TEXT,
    priorite        ENUM('basse','moyenne','haute','urgente') DEFAULT 'moyenne',
    etat            ENUM('a_faire','en_cours','terminee','annulee') DEFAULT 'a_faire',
    echeance        DATE,
    temps_estime    INT,            -- en minutes
    temps_passe     INT DEFAULT 0,  -- en minutes
    cree_le         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    modifie_le      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    FOREIGN KEY (categorie_id)   REFERENCES categories(id)
);

-- -------------------------------------------------------
--  TABLE : sous_taches
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS sous_taches (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    tache_id   INT          NOT NULL,
    titre      VARCHAR(200) NOT NULL,
    terminee   BOOLEAN      DEFAULT FALSE,
    ordre      INT          DEFAULT 0,
    FOREIGN KEY (tache_id) REFERENCES taches(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
--  TABLE : sessions_travail  (pour le suivi du temps)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS sessions_travail (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    tache_id   INT       NOT NULL,
    debut      DATETIME  NOT NULL,
    fin        DATETIME,
    duree      INT,      -- en secondes (calculé à la fermeture)
    FOREIGN KEY (tache_id) REFERENCES taches(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
--  TABLE : suggestions_ia  (historique des réponses IA)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS suggestions_ia (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id  INT       NOT NULL,
    tache_id        INT,      -- nullable : suggestion générale ou liée à une tâche
    type_suggestion ENUM('reformulation','priorites','decoupage','planification') NOT NULL,
    prompt          TEXT      NOT NULL,
    reponse         TEXT      NOT NULL,
    cree_le         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    FOREIGN KEY (tache_id)       REFERENCES taches(id) ON DELETE SET NULL
);

-- -------------------------------------------------------
--  VUE utile : stats par utilisateur
-- -------------------------------------------------------
CREATE OR REPLACE VIEW stats_utilisateur AS
SELECT
    u.id                                            AS utilisateur_id,
    u.nom,
    COUNT(t.id)                                     AS total_taches,
    SUM(t.etat = 'terminee')                        AS taches_terminees,
    ROUND(100.0 * SUM(t.etat = 'terminee') / NULLIF(COUNT(t.id), 0), 1) AS taux_completion,
    SUM(t.temps_passe)                              AS temps_total_minutes
FROM utilisateurs u
LEFT JOIN taches t ON t.utilisateur_id = u.id
GROUP BY u.id, u.nom;
