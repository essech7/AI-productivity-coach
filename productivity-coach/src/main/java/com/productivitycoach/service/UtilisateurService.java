package com.productivitycoach.service;

import com.productivitycoach.dao.DAOException;
import com.productivitycoach.dao.UtilisateurDAO;
import com.productivitycoach.exception.AppException;
import com.productivitycoach.model.Utilisateur;

import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs.
 * Couche intermédiaire entre les contrôleurs JavaFX et le DAO.
 *
 * Contient la validation et la logique applicative.
 */
public class UtilisateurService {

    private final UtilisateurDAO dao = new UtilisateurDAO();

    // Session active (utilisateur connecté)
    private static Utilisateur utilisateurConnecte;

    // ── Inscription ────────────────────────────────────────────────────────────

    public Utilisateur inscrire(String nom, String prenom,
                                String email, String motDePasse,
                                Utilisateur.RythmeTravail rythme) {
        // Validation
        if (nom.isBlank() || prenom.isBlank())
            throw new AppException("Le nom et le prénom sont obligatoires.");
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$"))
            throw new AppException("Adresse email invalide.");
        if (motDePasse.length() < 6)
            throw new AppException("Le mot de passe doit contenir au moins 6 caractères.");

        Utilisateur u = new Utilisateur(nom, prenom, email, motDePasse, rythme);
        try {
            int id = dao.insert(u);
            u.setId(id);
            return u;
        } catch (DAOException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    // ── Authentification ───────────────────────────────────────────────────────

    public Utilisateur connecter(String email, String motDePasse) {
        if (email.isBlank() || motDePasse.isBlank())
            throw new AppException("Email et mot de passe requis.");
        try {
            Optional<Utilisateur> opt = dao.authentifier(email, motDePasse);
            if (opt.isEmpty())
                throw new AppException("Email ou mot de passe incorrect.");
            utilisateurConnecte = opt.get();
            return utilisateurConnecte;
        } catch (DAOException e) {
            throw new AppException("Erreur de connexion : " + e.getMessage(), e);
        }
    }

    public void deconnecter() {
        utilisateurConnecte = null;
    }

    // ── Session ────────────────────────────────────────────────────────────────

    public static Utilisateur getUtilisateurConnecte() {
        if (utilisateurConnecte == null)
            throw new AppException("Aucun utilisateur connecté.");
        return utilisateurConnecte;
    }

    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    // ── Mise à jour profil ─────────────────────────────────────────────────────

    public void mettreAJourProfil(Utilisateur u) {
        try {
            dao.update(u);
            utilisateurConnecte = u;
        } catch (DAOException e) {
            throw new AppException("Erreur mise à jour profil : " + e.getMessage(), e);
        }
    }
}
