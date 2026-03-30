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
 * Contrôleur du formulaire d'inscription.
 */
public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> rythmeCombo;
    @FXML private TextArea objectifsArea;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;

    private final UtilisateurService service = new UtilisateurService();

    @FXML
    public void initialize() {
        rythmeCombo.getItems().addAll("Matin 🌅", "Soir 🌙", "Flexible ☀️");
        rythmeCombo.setValue("Flexible ☀️");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        clearError();

        String mdp     = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (!mdp.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }

        Utilisateur.RythmeTravail rythme = switch (rythmeCombo.getValue()) {
            case "Matin 🌅"    -> Utilisateur.RythmeTravail.MATIN;
            case "Soir 🌙"     -> Utilisateur.RythmeTravail.SOIR;
            default            -> Utilisateur.RythmeTravail.FLEXIBLE;
        };

        try {
            var u = service.inscrire(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                mdp, rythme
            );
            u.setObjectifs(objectifsArea.getText());
            // Connexion automatique après inscription
            service.connecter(emailField.getText().trim(), mdp);
            ouvrirDashboard();
        } catch (AppException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleRetourLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            showError("Erreur navigation : " + e.getMessage());
        }
    }

    private void ouvrirDashboard() throws Exception {
        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/DashboardView.fxml"));
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 750));
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}
