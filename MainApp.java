package com.coach.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        URL url = getClass().getResource("/views/LoginView.fxml");
        Parent root = FXMLLoader.load(url);
        
        Scene scene = new Scene(root, 400, 300); // Taille adaptée au login
        stage.setTitle("Coach Intelligent de Productivité - IA");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxmlView) throws Exception {
        URL url = MainApp.class.getResource("/views/" + fxmlView + ".fxml");
        Parent root = FXMLLoader.load(url);
        primaryStage.getScene().setRoot(root);
        if(fxmlView.equals("DashboardView")) {
            primaryStage.setWidth(850);
            primaryStage.setHeight(650);
            primaryStage.centerOnScreen();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
