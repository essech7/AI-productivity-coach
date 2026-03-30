package com.productivitycoach.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique DAO (Data Access Object).
 * Définit le contrat CRUD pour toutes les entités.
 *
 * ► Concept du cours Chapitre 1 : interfaces + polymorphisme
 *   Chaque DAO concret implémente cette interface.
 */
public interface DAO<T> {

    /**
     * Insère un nouvel objet en base et retourne l'ID généré.
     */
    int insert(T entity) throws DAOException;

    /**
     * Met à jour un objet existant.
     */
    boolean update(T entity) throws DAOException;

    /**
     * Supprime un objet par son ID.
     */
    boolean delete(int id) throws DAOException;

    /**
     * Récupère un objet par son ID.
     */
    Optional<T> findById(int id) throws DAOException;

    /**
     * Récupère tous les objets.
     */
    List<T> findAll() throws DAOException;
}
