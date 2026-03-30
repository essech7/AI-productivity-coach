package com.productivitycoach.model;

/**
 * Représente une sous-tâche liée à une tâche principale.
 * Générée manuellement ou par l'IA (découpage automatique).
 */
public class SousTache {

    private int id;
    private int tacheId;
    private String titre;
    private boolean terminee;
    private int ordre;

    public SousTache() {}

    public SousTache(int tacheId, String titre, int ordre) {
        this.tacheId  = tacheId;
        this.titre    = titre;
        this.ordre    = ordre;
        this.terminee = false;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public int getTacheId()                  { return tacheId; }
    public void setTacheId(int tacheId)      { this.tacheId = tacheId; }

    public String getTitre()                 { return titre; }
    public void setTitre(String titre)       { this.titre = titre; }

    public boolean isTerminee()              { return terminee; }
    public void setTerminee(boolean t)       { this.terminee = t; }

    public int getOrdre()                    { return ordre; }
    public void setOrdre(int ordre)          { this.ordre = ordre; }

    @Override
    public String toString() {
        return (terminee ? "[✓] " : "[ ] ") + titre;
    }
}
