package com.productivitycoach.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant une tâche de l'utilisateur.
 * Couche : Métier / Modèle
 */
public class Tache {

    private int id;
    private int utilisateurId;
    private Categorie categorie;
    private String titre;
    private String description;
    private Priorite priorite;
    private Etat etat;
    private LocalDate echeance;
    private int tempsEstime;   // minutes
    private int tempsPasse;    // minutes
    private LocalDateTime creeLe;
    private List<SousTache> sousTaches = new ArrayList<>();

    // ── Énumérations ──────────────────────────────────────────────────────────

    public enum Priorite {
        BASSE("Basse"), MOYENNE("Moyenne"), HAUTE("Haute"), URGENTE("Urgente");

        private final String libelle;
        Priorite(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public enum Etat {
        A_FAIRE("À faire"), EN_COURS("En cours"),
        TERMINEE("Terminée"), ANNULEE("Annulée");

        private final String libelle;
        Etat(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ── Constructeurs ──────────────────────────────────────────────────────────

    public Tache() {}

    public Tache(String titre, Categorie categorie,
                 Priorite priorite, LocalDate echeance) {
        this.titre     = titre;
        this.categorie = categorie;
        this.priorite  = priorite;
        this.echeance  = echeance;
        this.etat      = Etat.A_FAIRE;
    }

    // ── Méthodes métier ────────────────────────────────────────────────────────

    /**
     * Retourne le pourcentage de complétion basé sur les sous-tâches.
     */
    public int getPourcentageCompletion() {
        if (etat == Etat.TERMINEE) return 100;
        if (sousTaches.isEmpty())  return 0;
        long terminees = sousTaches.stream()
                                   .filter(SousTache::isTerminee)
                                   .count();
        return (int) (100.0 * terminees / sousTaches.size());
    }

    /**
     * Vérifie si la tâche est en retard.
     */
    public boolean isEnRetard() {
        return echeance != null
            && echeance.isBefore(LocalDate.now())
            && etat != Etat.TERMINEE
            && etat != Etat.ANNULEE;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public int getId()                             { return id; }
    public void setId(int id)                      { this.id = id; }

    public int getUtilisateurId()                  { return utilisateurId; }
    public void setUtilisateurId(int uid)          { this.utilisateurId = uid; }

    public Categorie getCategorie()                { return categorie; }
    public void setCategorie(Categorie c)          { this.categorie = c; }

    public String getTitre()                       { return titre; }
    public void setTitre(String titre)             { this.titre = titre; }

    public String getDescription()                 { return description; }
    public void setDescription(String desc)        { this.description = desc; }

    public Priorite getPriorite()                  { return priorite; }
    public void setPriorite(Priorite p)            { this.priorite = p; }

    public Etat getEtat()                          { return etat; }
    public void setEtat(Etat etat)                 { this.etat = etat; }

    public LocalDate getEcheance()                 { return echeance; }
    public void setEcheance(LocalDate e)           { this.echeance = e; }

    public int getTempsEstime()                    { return tempsEstime; }
    public void setTempsEstime(int t)              { this.tempsEstime = t; }

    public int getTempsPasse()                     { return tempsPasse; }
    public void setTempsPasse(int t)               { this.tempsPasse = t; }

    public LocalDateTime getCreeLe()               { return creeLe; }
    public void setCreeLe(LocalDateTime c)         { this.creeLe = c; }

    public List<SousTache> getSousTaches()         { return sousTaches; }
    public void setSousTaches(List<SousTache> st)  { this.sousTaches = st; }

    @Override
    public String toString() {
        return "Tache{id=" + id + ", titre='" + titre + "', etat=" + etat + "}";
    }
}
