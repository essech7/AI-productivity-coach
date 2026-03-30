package com.productivitycoach.dao;

import com.productivitycoach.model.Categorie;
import com.productivitycoach.model.Tache;
import com.productivitycoach.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation DAO pour les tâches.
 *
 * ► Chapitre 3 : requêtes paramétrées, JOIN, ResultSet.
 */
public class TacheDAO implements DAO<Tache> {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public int insert(Tache t) throws DAOException {
        String sql = """
            INSERT INTO taches
              (utilisateur_id, categorie_id, titre, description,
               priorite, etat, echeance, temps_estime)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, t.getUtilisateurId());
            ps.setInt   (2, t.getCategorie().getId());
            ps.setString(3, t.getTitre());
            ps.setString(4, t.getDescription());
            ps.setString(5, t.getPriorite().name().toLowerCase());
            ps.setString(6, t.getEtat().name().toLowerCase());
            if (t.getEcheance() != null)
                ps.setDate(7, Date.valueOf(t.getEcheance()));
            else
                ps.setNull(7, Types.DATE);
            ps.setInt   (8, t.getTempsEstime());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur insertion tâche : " + e.getMessage(), e);
        }
        throw new DAOException("Insertion tâche échouée.");
    }

    @Override
    public boolean update(Tache t) throws DAOException {
        String sql = """
            UPDATE taches
            SET categorie_id=?, titre=?, description=?,
                priorite=?, etat=?, echeance=?, temps_estime=?, temps_passe=?
            WHERE id=?
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, t.getCategorie().getId());
            ps.setString(2, t.getTitre());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getPriorite().name().toLowerCase());
            ps.setString(5, t.getEtat().name().toLowerCase());
            if (t.getEcheance() != null)
                ps.setDate(6, Date.valueOf(t.getEcheance()));
            else
                ps.setNull(6, Types.DATE);
            ps.setInt(7, t.getTempsEstime());
            ps.setInt(8, t.getTempsPasse());
            ps.setInt(9, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur mise à jour tâche : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        try (PreparedStatement ps = getConn().prepareStatement(
                "DELETE FROM taches WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Erreur suppression tâche : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Tache> findById(int id) throws DAOException {
        String sql = """
            SELECT t.*, c.id AS cat_id, c.nom AS cat_nom,
                   c.couleur AS cat_couleur, c.icone AS cat_icone
            FROM taches t
            JOIN categories c ON c.id = t.categorie_id
            WHERE t.id = ?
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur recherche tâche : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Tache> findAll() throws DAOException {
        return findByUtilisateur(-1); // usage interne seulement
    }

    /**
     * Récupère toutes les tâches d'un utilisateur donné.
     */
    public List<Tache> findByUtilisateur(int utilisateurId) throws DAOException {
        String sql = """
            SELECT t.*, c.id AS cat_id, c.nom AS cat_nom,
                   c.couleur AS cat_couleur, c.icone AS cat_icone
            FROM taches t
            JOIN categories c ON c.id = t.categorie_id
            WHERE t.utilisateur_id = ?
            ORDER BY
                FIELD(t.priorite, 'urgente','haute','moyenne','basse'),
                t.echeance ASC
            """;
        List<Tache> liste = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur récupération tâches : " + e.getMessage(), e);
        }
        return liste;
    }

    // ── MAPPING ───────────────────────────────────────────────────────────────

    private Tache map(ResultSet rs) throws SQLException {
        Tache t = new Tache();
        t.setId(rs.getInt("id"));
        t.setUtilisateurId(rs.getInt("utilisateur_id"));
        t.setTitre(rs.getString("titre"));
        t.setDescription(rs.getString("description"));
        t.setPriorite(Tache.Priorite.valueOf(rs.getString("priorite").toUpperCase()));
        t.setEtat(Tache.Etat.valueOf(rs.getString("etat").toUpperCase()));
        Date d = rs.getDate("echeance");
        if (d != null) t.setEcheance(d.toLocalDate());
        t.setTempsEstime(rs.getInt("temps_estime"));
        t.setTempsPasse(rs.getInt("temps_passe"));
        Timestamp ts = rs.getTimestamp("cree_le");
        if (ts != null) t.setCreeLe(ts.toLocalDateTime());

        Categorie cat = new Categorie(
            rs.getInt("cat_id"),
            rs.getString("cat_nom"),
            rs.getString("cat_couleur"),
            rs.getString("cat_icone")
        );
        t.setCategorie(cat);
        return t;
    }
}
