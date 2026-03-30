package com.productivitycoach.ui.controllers;

import com.productivitycoach.exception.AppException;
import com.productivitycoach.model.Utilisateur;
import com.productivitycoach.service.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Contrôleur de la vue de connexion / inscription.
 *
 * Gère :
 *   - La connexion d'un utilisateur existant
 *   - La redirection vers la vue principale après authentification
 *   - L'affichage des erreurs de saisie
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    private final UtilisateurService service = new UtilisateurService();

    // ── Connexion ──────────────────────────────────────────────────────────────

    @FXML
    public void handleLogin(ActionEvent event) {
        clearError();
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText();

        try {
            service.connecter(email, mdp);
            ouvrirDashboard();
        } catch (AppException e) {
            showError(e.getMessage());
        }
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    @FXML
    public void handleRegisterLink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/RegisterView.fxml")
            );
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            showError("Impossible d'ouvrir la page d'inscription.");
        }
    }

    private void ouvrirDashboard() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/DashboardView.fxml")
            );
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 750));
            stage.setTitle("AI Productivity Coach — "
                + UtilisateurService.getUtilisateurConnecte().getNomComplet());
        } catch (Exception e) {
            showError("Erreur chargement du dashboard : " + e.getMessage());
        }
    }

    // ── Utilitaires UI ─────────────────────────────────────────────────────────

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}
