package dao;

import model.Membre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 1 — Connexion
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Connection connection;

    public MembreDAO() {
        this.connection = Database.getConnection();
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 2 — CREATE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void create(Membre m) {
        String sql = "INSERT INTO membres (nom, prenom, email, telephone, dateNaissance, actif) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getEmail());
            stmt.setString(4, m.getTelephone());
            stmt.setDate(5, Date.valueOf(m.getDateNaissance()));
            stmt.setBoolean(6, m.isActif());
            stmt.executeUpdate();
            System.out.println("Membre ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur create : " + e.getMessage());
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 3 — READ ALL
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public List<Membre> findAll() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getBoolean("actif"));
                membres.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll : " + e.getMessage());
        }
        return membres;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 4 — UPDATE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void update(Membre m) {
        String sql = "UPDATE membres SET nom=?, prenom=?, email=?, "
                + "telephone=?, dateNaissance=?, actif=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, m.getNom());
            stmt.setString(2, m.getPrenom());
            stmt.setString(3, m.getEmail());
            stmt.setString(4, m.getTelephone());
            stmt.setDate(5, Date.valueOf(m.getDateNaissance()));
            stmt.setBoolean(6, m.isActif());
            stmt.setInt(7, m.getId());
            stmt.executeUpdate();
            System.out.println("Membre modifié !");
        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 5 — DELETE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void delete(int id) {
        String sql = "DELETE FROM membres WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Membre supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur delete : " + e.getMessage());
        }
    }
}