package com.productivitycoach;

import com.productivitycoach.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application AI Productivity Coach.
 * Lance l'interface JavaFX et initialise la connexion BD.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Vérifier la connexion BD au démarrage
        DatabaseConnection.getInstance().testConnection();

        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/LoginView.fxml")
        );

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
            getClass().getResource("/css/app.css").toExternalForm()
        );

        primaryStage.setTitle("AI Productivity Coach");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Fermer la connexion BD proprement à la sortie
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
