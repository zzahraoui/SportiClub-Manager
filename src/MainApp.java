import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import view.AbonnementView;
import view.DashboardView;
import view.MembreView;
import javafx.scene.control.ButtonBar;
import util.CsvExporter;
import dao.MembreDAO;
import dao.AbonnementDAO;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 1 — Layout racine
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        BorderPane root = new BorderPane();

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 2 — MenuBar (TOP)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        MenuBar menuBar = new MenuBar();

        Menu menuFichier = new Menu("Fichier");
        MenuItem itemExport = new MenuItem("Exporter CSV");

        itemExport.setOnAction(e -> {
            // FileChooser — boîte de dialogue
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Exporter les données");
            fileChooser.setInitialFileName("membres.csv");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            java.io.File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                // Choix du type d'export
                Alert choix = new Alert(Alert.AlertType.CONFIRMATION);
                choix.setTitle("Type d'export");
                choix.setHeaderText("Que voulez-vous exporter ?");

                ButtonType btnMembresExport = new ButtonType("Membres");
                ButtonType btnAbonnementsExport = new ButtonType("Abonnements");
                ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

                choix.getButtonTypes().setAll(btnMembresExport, btnAbonnementsExport, btnAnnuler);
                choix.showAndWait().ifPresent(response -> {
                    if (response == btnMembresExport) {
                        CsvExporter.exportMembres(
                                new MembreDAO().findAll(), file.getAbsolutePath());
                        Alert ok = new Alert(Alert.AlertType.INFORMATION);
                        ok.setTitle("Export reussi");
                        ok.setContentText("Fichier sauvegarde : " + file.getName());
                        ok.showAndWait();
                    } else if (response == btnAbonnementsExport) {
                        CsvExporter.exportAbonnements(
                                new AbonnementDAO().findAll(), file.getAbsolutePath());
                        Alert ok = new Alert(Alert.AlertType.INFORMATION);
                        ok.setTitle("Export reussi");
                        ok.setContentText("Fichier sauvegarde : " + file.getName());
                        ok.showAndWait();
                    }
                });
            }
        });
        MenuItem itemQuitter = new MenuItem("Quitter");

        itemQuitter.setOnAction(e -> primaryStage.close());
        menuFichier.getItems().addAll(itemExport, itemQuitter);

        Menu menuAide = new Menu("Aide");
        MenuItem itemAPropos = new MenuItem("À propos");
        menuAide.getItems().add(itemAPropos);

        menuBar.getMenus().addAll(menuFichier, menuAide);
        root.setTop(menuBar);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 3 — Navigation gauche (LEFT)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(15));
        navBar.getStyleClass().add("sidebar");
        navBar.setMinWidth(180);

        Button btnMembres = new Button("👤 Membres");
        Button btnAbonnements = new Button("📋 Abonnements");
        Button btnDashboard = new Button("📊 Dashboard");

        for (Button btn : new Button[] { btnMembres, btnAbonnements, btnDashboard }) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.getStyleClass().add("nav-button");
        }

        navBar.getChildren().addAll(
                new Label("") {
                    {
                        setStyle("-fx-text-fill:white; -fx-font-weight:bold;");
                    }
                },
                btnMembres, btnAbonnements, btnDashboard);
        root.setLeft(navBar);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 4 — Zone centrale (CENTER)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        StackPane center = new StackPane();
        center.getStyleClass().add("content-pane");

        Label welcome = new Label("Bienvenue — Club Sportif");
        welcome.getStyleClass().add("hero-title");
        center.getChildren().add(welcome);
        root.setCenter(center);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 5 — Status bar (BOTTOM)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Label statusBar = new Label("  ✅ Connecté à club_sportif");
        statusBar.getStyleClass().add("status-bar");
        statusBar.setMaxWidth(Double.MAX_VALUE);
        root.setBottom(statusBar);

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 6 — Navigation entre vues
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        btnMembres.setOnAction(e -> {
            center.getChildren().clear();
            MembreView membreView = new MembreView();
            center.getChildren().add(membreView.getView());
        });

        btnAbonnements.setOnAction(e -> {
            center.getChildren().clear();
            AbonnementView abonnementView = new AbonnementView();
            center.getChildren().add(abonnementView.getView());
        });

        btnDashboard.setOnAction(e -> {
            center.getChildren().clear();
            DashboardView dashboardView = new DashboardView();
            center.getChildren().add(dashboardView.getView());
        });

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // BLOC 7 — Scène et Stage
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/view/style-neon.css").toExternalForm());
        primaryStage.setTitle("Club Sportif — GI3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}