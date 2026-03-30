package com.productivitycoach.service;

import com.productivitycoach.dao.DAOException;
import com.productivitycoach.dao.TacheDAO;
import com.productivitycoach.exception.AppException;
import com.productivitycoach.model.Tache;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des tâches.
 */
public class TacheService {

    private final TacheDAO dao = new TacheDAO();

    public Tache creer(Tache tache) {
        if (tache.getTitre() == null || tache.getTitre().isBlank())
            throw new AppException("Le titre de la tâche est obligatoire.");
        if (tache.getCategorie() == null)
            throw new AppException("La catégorie est obligatoire.");

        tache.setUtilisateurId(UtilisateurService.getUtilisateurConnecte().getId());
        try {
            int id = dao.insert(tache);
            tache.setId(id);
            return tache;
        } catch (DAOException e) {
            throw new AppException("Erreur création tâche : " + e.getMessage(), e);
        }
    }

    public void modifier(Tache tache) {
        try {
            dao.update(tache);
        } catch (DAOException e) {
            throw new AppException("Erreur modification tâche : " + e.getMessage(), e);
        }
    }

    public void supprimer(int id) {
        try {
            dao.delete(id);
        } catch (DAOException e) {
            throw new AppException("Erreur suppression tâche : " + e.getMessage(), e);
        }
    }

    public List<Tache> getMesTaches() {
        try {
            return dao.findByUtilisateur(
                UtilisateurService.getUtilisateurConnecte().getId()
            );
        } catch (DAOException e) {
            throw new AppException("Erreur chargement tâches : " + e.getMessage(), e);
        }
    }

    public Optional<Tache> findById(int id) {
        try {
            return dao.findById(id);
        } catch (DAOException e) {
            throw new AppException("Erreur recherche tâche : " + e.getMessage(), e);
        }
    }

    /**
     * Calcule le taux de complétion global de l'utilisateur.
     */
    public double getTauxCompletion() {
        List<Tache> taches = getMesTaches();
        if (taches.isEmpty()) return 0.0;
        long terminees = taches.stream()
                               .filter(t -> t.getEtat() == Tache.Etat.TERMINEE)
                               .count();
        return 100.0 * terminees / taches.size();
    }
}
