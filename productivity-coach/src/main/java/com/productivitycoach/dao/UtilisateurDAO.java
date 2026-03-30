package com.productivitycoach.dao;

import com.productivitycoach.model.Utilisateur;
import com.productivitycoach.util.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation DAO pour la gestion des utilisateurs.
 *
 * ► Chapitre 1 : implémentation d'une interface (DAO<Utilisateur>)
 * ► Chapitre 2 : gestion des exceptions SQL → DAOException
 * ► Chapitre 3 : PreparedStatement, ResultSet, JDBC complet
 */
public class UtilisateurDAO implements DAO<Utilisateur> {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── INSERT ─────────────────────────────────────────────────────────────────

    @Override
    public int insert(Utilisateur u) throws DAOException {
        String sql = """
            INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, objectifs, rythme)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            // Hash BCrypt avant insertion
            ps.setString(4, BCrypt.hashpw(u.getMotDePasse(), BCrypt.gensalt()));
            ps.setString(5, u.getObjectifs());
            ps.setString(6, u.getRythme() != null ? u.getRythme().name().toLowerCase() : "flexible");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // duplicate email
                throw new DAOException("Un compte avec cet email existe déjà.", e);
            }
            throw new DAOException("Erreur lors de l'inscription : " + e.getMessage(), e);
        }
        throw new DAOException("Insertion échouée, aucun ID généré.");
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────

    @Override
    public boolean update(Utilisateur u) throws DAOException {
        String sql = """
            UPDATE utilisateurs
            SET nom=?, prenom=?, objectifs=?, rythme=?
            WHERE id=?
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getObjectifs());
            ps.setString(4, u.getRythme().name().toLowerCase());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour utilisateur : " + e.getMessage(), e);
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────

    @Override
    public boolean delete(int id) throws DAOException {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM utilisateurs WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur suppression : " + e.getMessage(), e);
        }
    }

    // ── FIND BY ID ─────────────────────────────────────────────────────────────

    @Override
    public Optional<Utilisateur> findById(int id) throws DAOException {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM utilisateurs WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche utilisateur : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ── FIND ALL ───────────────────────────────────────────────────────────────

    @Override
    public List<Utilisateur> findAll() throws DAOException {
        List<Utilisateur> liste = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs  = st.executeQuery("SELECT * FROM utilisateurs")) {
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            throw new DAOException("Erreur récupération utilisateurs : " + e.getMessage(), e);
        }
        return liste;
    }

    // ── AUTHENTIFICATION ───────────────────────────────────────────────────────

    /**
     * Authentifie un utilisateur par email + mot de passe.
     * Vérifie le hash BCrypt stocké en base.
     *
     * @return l'utilisateur si authentifié, Optional.empty() sinon.
     */
    public Optional<Utilisateur> authentifier(String email,
                                               String motDePasse) throws DAOException {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM utilisateurs WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashStocke = rs.getString("mot_de_passe");
                    if (BCrypt.checkpw(motDePasse, hashStocke)) {
                        return Optional.of(map(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur d'authentification : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ── MAPPING ResultSet → Utilisateur ───────────────────────────────────────

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setObjectifs(rs.getString("objectifs"));
        String r = rs.getString("rythme");
        if (r != null) u.setRythme(Utilisateur.RythmeTravail.valueOf(r.toUpperCase()));
        Timestamp ts = rs.getTimestamp("cree_le");
        if (ts != null) u.setCreeLe(ts.toLocalDateTime());
        return u;
    }
}
