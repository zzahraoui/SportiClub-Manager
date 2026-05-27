package view;

import dao.AbonnementDAO;
import dao.MembreDAO;
import model.Abonnement;
import model.Membre;
import model.StatutAbonnement;
import model.TypeOffre;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView {

    private MembreDAO membreDAO = new MembreDAO();
    private AbonnementDAO abonnementDAO = new AbonnementDAO();

    public VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // Titre
        Label titre = new Label("Tableau de Bord");
        titre.getStyleClass().add("page-title");

        // Données depuis BDD
        List<Membre> membres = membreDAO.findAll();
        List<Abonnement> abonnements = abonnementDAO.findAll();

        int totalMembres = membres.size();
        int membresActifs = (int) membres.stream().filter(Membre::isActif).count();
        int aboActifs = (int) abonnements.stream()
                .filter(a -> a.getStatut() == StatutAbonnement.ACTIF).count();
        double revenuTotal = abonnements.stream()
                .filter(a -> a.getStatut() == StatutAbonnement.ACTIF)
                .mapToDouble(Abonnement::getPrixMensuel).sum();

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 1 — Cartes statistiques
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        HBox cartes = new HBox(15);
        cartes.getChildren().addAll(
                buildCarte("👥 Total Membres", String.valueOf(totalMembres), "#1b2129"),
                buildCarte("✅ Membres Actifs", String.valueOf(membresActifs), "#202630"),
                buildCarte("📋 Abonnements Actifs", String.valueOf(aboActifs), "#171b21"),
                buildCarte("💰 Revenu Mensuel", String.format("%.0f DHS", revenuTotal), "#2a2f37"));

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 2 — ProgressBar taux d'activité
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        double tauxActivite = totalMembres > 0
                ? (double) membresActifs / totalMembres
                : 0;

        Label lblTaux = new Label(String.format(
                "Taux d'activité : %d/%d membres actifs (%.0f%%)",
                membresActifs, totalMembres, tauxActivite * 100));
        lblTaux.getStyleClass().add("subtle-label");

        ProgressBar progressBar = new ProgressBar(tauxActivite);
        progressBar.setPrefWidth(400);
        progressBar.setPrefHeight(20);

        VBox blocProgress = new VBox(8, lblTaux, progressBar);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 3 — Répartition par type d'offre
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Map<TypeOffre, Long> repartition = abonnements.stream()
                .collect(Collectors.groupingBy(Abonnement::getTypeOffre, Collectors.counting()));

        Label lblRepartition = new Label("Répartition par type d'offre :");
        lblRepartition.getStyleClass().add("section-title");

        ListView<String> listViewStats = new ListView<>();
        for (TypeOffre type : TypeOffre.values()) {
            long count = repartition.getOrDefault(type, 0L);
            double pourcentage = abonnements.isEmpty() ? 0
                    : (double) count / abonnements.size() * 100;
            listViewStats.getItems().add(
                    String.format("%-20s → %d abonnement(s)  (%.0f%%)",
                            type.name(), count, pourcentage));
        }
        listViewStats.setPrefHeight(120);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 4 — Abonnements expirant bientôt
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Label lblExpiration = new Label("⚠️ Abonnements expirés :");
        lblExpiration.getStyleClass().add("warning-title");

        ListView<String> listExpires = new ListView<>();
        abonnements.stream()
                .filter(a -> a.getStatut() == StatutAbonnement.EXPIRE)
                .forEach(a -> listExpires.getItems().add(
                        String.format("ID:%d | %s | Fin: %s",
                                a.getId(), a.getTypeOffre(), a.getDateFin())));

        if (listExpires.getItems().isEmpty()) {
            listExpires.getItems().add("Aucun abonnement expiré");
        }
        listExpires.setPrefHeight(100);

        // Assemblage final
        root.getChildren().addAll(
                titre, cartes,
                new Separator(),
                blocProgress,
                new Separator(),
                lblRepartition, listViewStats,
                new Separator(),
                lblExpiration, listExpires);

        return root;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // Carte statistique réutilisable
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private VBox buildCarte(String titre, String valeur, String couleur) {
        Label lblTitre = new Label(titre);
        Label lblValeur = new Label(valeur);

        lblTitre.getStyleClass().add("stat-card-title");
        lblValeur.getStyleClass().add("stat-card-value");

        VBox carte = new VBox(8, lblTitre, lblValeur);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(20));
        carte.setPrefWidth(160);
        carte.getStyleClass().add("stat-card");
        carte.setStyle("-fx-background-color: " + couleur + ";");

        return carte;
    }
}