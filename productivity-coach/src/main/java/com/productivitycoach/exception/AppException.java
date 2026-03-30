package com.productivitycoach.exception;

/**
 * Exception applicative générale.
 * Hérite de RuntimeException pour ne pas forcer le try-catch
 * dans les contrôleurs JavaFX.
 *
 * ► Concept Chapitre 2 : exceptions personnalisées + hiérarchie.
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
