package com.productivitycoach.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant un utilisateur de l'application.
 * Couche : Métier / Modèle
 */
public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;   // stocké en BCrypt hash
    private String objectifs;
    private RythmeTravail rythme;
    private LocalDateTime creeLe;

    public enum RythmeTravail {
        MATIN, SOIR, FLEXIBLE
    }

    // ── Constructeurs ──────────────────────────────────────────────────────────

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email,
                       String motDePasse, RythmeTravail rythme) {
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
        this.rythme     = rythme;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getNom()                    { return nom; }
    public void setNom(String nom)            { this.nom = nom; }

    public String getPrenom()                 { return prenom; }
    public void setPrenom(String prenom)      { this.prenom = prenom; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getMotDePasse()             { return motDePasse; }
    public void setMotDePasse(String mdp)     { this.motDePasse = mdp; }

    public String getObjectifs()              { return objectifs; }
    public void setObjectifs(String obj)      { this.objectifs = obj; }

    public RythmeTravail getRythme()          { return rythme; }
    public void setRythme(RythmeTravail r)    { this.rythme = r; }

    public LocalDateTime getCreeLe()          { return creeLe; }
    public void setCreeLe(LocalDateTime c)    { this.creeLe = c; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", email='" + email + "'}";
    }
}
