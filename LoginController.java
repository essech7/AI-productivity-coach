package com.coach.app.controllers;

import com.coach.app.MainApp;
import com.coach.app.dao.UserDAO;
import com.coach.app.models.SessionContext;
import com.coach.app.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            SessionContext.setCurrentUser(user);
            try {
                MainApp.setRoot("DashboardView");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Identifiants incorrects.");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Remplissez pour vous inscrire.");
            return;
        }

        User newUser = new User(0, username, password, "", "");
        boolean success = userDAO.register(newUser);
        if (success) {
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Inscription réussie, connectez-vous !");
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("L'utilisateur existe déjà ou erreur DB.");
        }
    }
}
