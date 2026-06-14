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
import javafx.scene.chart.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
                FlowPane cartes = new FlowPane(15, 15);
                cartes.getStyleClass().add("card-grid");
                cartes.setPrefWrapLength(780);
                cartes.getChildren().addAll(
                                buildCarte("👥 Total Membres", String.valueOf(totalMembres), "#131820"),
                                buildCarte("✅ Membres Actifs", String.valueOf(membresActifs), "#162129"),
                                buildCarte("📋 Abonnements Actifs", String.valueOf(aboActifs), "#11161d"),
                                buildCarte("💰 Revenu Mensuel", String.format("%.0f DHS", revenuTotal), "#182631"));

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
                // BLOC 3 — Charts
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Map<TypeOffre, Long> repartition = abonnements.stream()
                                .collect(Collectors.groupingBy(Abonnement::getTypeOffre, Collectors.counting()));

                PieChart pieTypeOffre = new PieChart();
                pieTypeOffre.setLegendVisible(false);
                pieTypeOffre.setLabelsVisible(true);
                for (TypeOffre type : TypeOffre.values()) {
                        long count = repartition.getOrDefault(type, 0L);
                        if (count > 0) {
                                pieTypeOffre.getData().add(new PieChart.Data(type.name(), count));
                        }
                }
                if (pieTypeOffre.getData().isEmpty()) {
                        pieTypeOffre.getData().add(new PieChart.Data("Aucune", 1));
                }

                CategoryAxis statusAxis = new CategoryAxis();
                NumberAxis statusValue = new NumberAxis();
                BarChart<String, Number> statusChart = new BarChart<>(statusAxis, statusValue);
                statusChart.setLegendVisible(false);
                XYChart.Series<String, Number> statusSeries = new XYChart.Series<>();
                statusSeries.getData().add(new XYChart.Data<>("ACTIF", aboActifs));
                statusSeries.getData().add(new XYChart.Data<>("EXPIRE",
                                abonnements.stream().filter(a -> a.getStatut() == StatutAbonnement.EXPIRE).count()));
                statusSeries.getData().add(new XYChart.Data<>("SUSPENDU",
                                abonnements.stream().filter(a -> a.getStatut() == StatutAbonnement.SUSPENDU).count()));
                statusChart.getData().add(statusSeries);

                CategoryAxis revenueAxis = new CategoryAxis();
                NumberAxis revenueValue = new NumberAxis();
                LineChart<String, Number> revenueChart = new LineChart<>(revenueAxis, revenueValue);
                revenueChart.setLegendVisible(false);
                XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();

                Map<YearMonth, Double> revenueByMonth = new TreeMap<>();
                for (Abonnement a : abonnements) {
                        if (a.getDateDebut() == null) {
                                continue;
                        }
                        YearMonth month = YearMonth.from(a.getDateDebut());
                        double value = a.getStatut() == StatutAbonnement.ACTIF ? a.getPrixMensuel() : 0;
                        revenueByMonth.merge(month, value, Double::sum);
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
                for (Map.Entry<YearMonth, Double> entry : revenueByMonth.entrySet()) {
                        revenueSeries.getData()
                                        .add(new XYChart.Data<>(entry.getKey().format(formatter), entry.getValue()));
                }
                if (revenueSeries.getData().isEmpty()) {
                        YearMonth now = YearMonth.now();
                        revenueSeries.getData().add(new XYChart.Data<>(now.format(formatter), 0));
                }
                revenueChart.getData().add(revenueSeries);

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
                if (membres.isEmpty() && abonnements.isEmpty()) {
                        Label empty = new Label("Aucune donnee disponible. Verifiez la base MySQL.");
                        empty.getStyleClass().add("empty-state");
                        root.getChildren().addAll(titre, empty);
                        return root;
                }

                HBox chartsRow = new HBox(15,
                                buildChartPanel("Offres", pieTypeOffre),
                                buildChartPanel("Statut", statusChart));
                HBox revenueRow = new HBox(15,
                                buildChartPanel("Revenu par mois", revenueChart));

                root.getChildren().addAll(
                                titre, cartes,
                                new Separator(),
                                blocProgress,
                                new Separator(),
                                chartsRow,
                                revenueRow,
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

        private VBox buildChartPanel(String titre, Chart chart) {
                Label label = new Label(titre);
                label.getStyleClass().add("section-title");

                VBox panel = new VBox(10, label, chart);
                panel.getStyleClass().add("chart-panel");
                panel.setPadding(new Insets(12));
                panel.setPrefWidth(360);

                chart.setPrefHeight(220);
                chart.getStyleClass().add("chart-surface");
                return panel;
        }
}