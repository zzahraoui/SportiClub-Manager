package dao;

import model.Abonnement;
import model.TypeOffre;
import model.StatutAbonnement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementDAO {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 1 — Connexion
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Connection connection;

    public AbonnementDAO() {
        this.connection = Database.getConnection();
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 2 — CREATE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void create(Abonnement a) {
        String sql = "INSERT INTO abonnements (typeOffre, prixMensuel, dureeEngagement, "
                + "dateDebut, statut, membreId) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, a.getTypeOffre().name());
            stmt.setDouble(2, a.getPrixMensuel());
            stmt.setInt(3, a.getDureeEngagement());
            stmt.setDate(4, Date.valueOf(a.getDateDebut()));
            stmt.setString(5, a.getStatut().name());
            stmt.setInt(6, a.getMembreId());
            stmt.executeUpdate();
            System.out.println("Abonnement ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur create : " + e.getMessage());
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 3 — READ ALL
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public List<Abonnement> findAll() {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnements";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Abonnement a = new Abonnement(
                        rs.getInt("id"),
                        TypeOffre.valueOf(rs.getString("typeOffre")),
                        rs.getDouble("prixMensuel"),
                        rs.getInt("dureeEngagement"),
                        rs.getDate("dateDebut").toLocalDate(),
                        StatutAbonnement.valueOf(rs.getString("statut")),
                        rs.getInt("membreId"));
                abonnements.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll : " + e.getMessage());
        }
        return abonnements;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 4 — READ BY MEMBRE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public List<Abonnement> findByMembre(int membreId) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnements WHERE membreId = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Abonnement a = new Abonnement(
                        rs.getInt("id"),
                        TypeOffre.valueOf(rs.getString("typeOffre")),
                        rs.getDouble("prixMensuel"),
                        rs.getInt("dureeEngagement"),
                        rs.getDate("dateDebut").toLocalDate(),
                        StatutAbonnement.valueOf(rs.getString("statut")),
                        rs.getInt("membreId"));
                abonnements.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Erreur findByMembre : " + e.getMessage());
        }
        return abonnements;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 5 — UPDATE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void update(Abonnement a) {
        String sql = "UPDATE abonnements SET typeOffre=?, prixMensuel=?, "
                + "dureeEngagement=?, dateDebut=?, statut=?, membreId=? "
                + "WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, a.getTypeOffre().name());
            stmt.setDouble(2, a.getPrixMensuel());
            stmt.setInt(3, a.getDureeEngagement());
            stmt.setDate(4, Date.valueOf(a.getDateDebut()));
            stmt.setString(5, a.getStatut().name());
            stmt.setInt(6, a.getMembreId());
            stmt.setInt(7, a.getId());
            stmt.executeUpdate();
            System.out.println("Abonnement modifié !");
        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 6 — DELETE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public void delete(int id) {
        String sql = "DELETE FROM abonnements WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Abonnement supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur delete : " + e.getMessage());
        }
    }
}