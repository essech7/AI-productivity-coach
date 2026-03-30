package com.productivitycoach.dao;

/**
 * Exception personnalisée pour les erreurs de la couche DAO.
 *
 * ► Concept du cours Chapitre 2 : exceptions personnalisées.
 *   On encapsule les SQLException pour ne pas exposer
 *   les détails JDBC aux couches supérieures.
 */
public class DAOException extends Exception {

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
