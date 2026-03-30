package com.productivitycoach.model;

/**
 * Représente une catégorie de tâche (Études, Travail, Personnel...).
 */
public class Categorie {

    private int id;
    private String nom;
    private String couleur;
    private String icone;

    public Categorie() {}

    public Categorie(int id, String nom, String couleur, String icone) {
        this.id      = id;
        this.nom     = nom;
        this.couleur = couleur;
        this.icone   = icone;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public String getNom()               { return nom; }
    public void setNom(String nom)       { this.nom = nom; }

    public String getCouleur()           { return couleur; }
    public void setCouleur(String c)     { this.couleur = c; }

    public String getIcone()             { return icone; }
    public void setIcone(String i)       { this.icone = i; }

    @Override
    public String toString() { return nom; }
}
