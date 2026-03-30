package com.productivitycoach.util;

import com.productivitycoach.exception.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton de connexion JDBC.
 * Fournit une unique connexion partagée à la base de données.
 *
 * ► Concept du cours Chapitre 3 : API JDBC, DriverManager, Connection.
 *
 * CONFIGURATION : modifiez les constantes DB_* selon votre environnement.
 */
public class DatabaseConnection {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/productivity_coach";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "votre_mot_de_passe";
    // Pour PostgreSQL : "jdbc:postgresql://localhost:5432/productivity_coach"
    // ──────────────────────────────────────────────────────────────────────────

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    /**
     * Retourne l'instance unique (pattern Singleton).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Retourne la connexion JDBC, en la (re)créant si nécessaire.
     *
     * ► Chapitre 3 : DriverManager.getConnection()
     */
    public Connection getConnection() throws AppException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Connexion établie avec succès.");
            }
            return connection;
        } catch (SQLException e) {
            throw new AppException("Impossible de se connecter à la base de données : "
                                   + e.getMessage(), e);
        }
    }

    /**
     * Teste la connexion au démarrage de l'application.
     */
    public void testConnection() {
        try {
            getConnection();
        } catch (AppException e) {
            System.err.println("[DB] ⚠ Connexion échouée : " + e.getMessage());
            // L'app continue — certaines fonctionnalités seront indisponibles
        }
    }

    /**
     * Ferme proprement la connexion.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erreur à la fermeture : " + e.getMessage());
        }
    }
}
