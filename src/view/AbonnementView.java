package view;

import dao.AbonnementDAO;
import dao.MembreDAO;
import model.Abonnement;
import model.Membre;
import model.TypeOffre;
import model.StatutAbonnement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class AbonnementView {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 1 — DAO & Données
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private AbonnementDAO abonnementDAO = new AbonnementDAO();
    private MembreDAO membreDAO = new MembreDAO();
    private ObservableList<Abonnement> abonnementList = FXCollections.observableArrayList();
    private TableView<Abonnement> tableView = new TableView<>();

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 2 — Champs formulaire
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private ComboBox<TypeOffre> cbTypeOffre = new ComboBox<>();
    private Spinner<Integer> spDuree = new Spinner<>(1, 24, 12);
    private Slider slPrix = new Slider(100, 500, 290);
    private Label lblPrix = new Label("290 DHS");
    private DatePicker dpDebut = new DatePicker();
    private ComboBox<StatutAbonnement> cbStatut = new ComboBox<>();
    private ComboBox<Membre> cbMembre = new ComboBox<>();
    private TextField tfRecherche = new TextField();

    private Abonnement abonnementSelectionne = null;

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // MÉTHODE PRINCIPALE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public VBox getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label titre = new Label("Gestion des Abonnements");
        titre.getStyleClass().add("page-title");

        root.getChildren().addAll(
                titre,
                buildFormulaire(),
                buildRecherche(),
                buildTableView());

        refreshTable();
        return root;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 3 — Formulaire
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Accordion buildFormulaire() {

        // Remplir les ComboBox
        cbTypeOffre.getItems().addAll(TypeOffre.values());
        cbTypeOffre.setValue(TypeOffre.CLASSIQUE);

        cbStatut.getItems().addAll(StatutAbonnement.values());
        cbStatut.setValue(StatutAbonnement.ACTIF);

        cbMembre.getItems().addAll(membreDAO.findAll());
        cbMembre.setPromptText("Sélectionner un membre");

        // Spinner — éditable
        spDuree.setEditable(true);
        spDuree.setTooltip(new Tooltip("Durée en mois (1-24)"));

        // Slider prix — affichage dynamique
        slPrix.setShowTickLabels(true);
        slPrix.setShowTickMarks(true);
        slPrix.setMajorTickUnit(100);
        slPrix.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblPrix.setText(String.format("%.0f DHS", newVal.doubleValue()));
        });

        // Tooltips
        cbTypeOffre.setTooltip(new Tooltip("Choisir le type d'offre"));
        dpDebut.setTooltip(new Tooltip("Date de début de l'abonnement"));
        cbMembre.setTooltip(new Tooltip("Membre concerné par cet abonnement"));

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("Type offre :"), cbTypeOffre,
                new Label("Membre :"), cbMembre);
        grid.addRow(1, new Label("Durée (mois) :"), spDuree,
                new Label("Date début :"), dpDebut);
        grid.addRow(2, new Label("Prix mensuel :"), slPrix,
                lblPrix);
        grid.addRow(3, new Label("Statut :"), cbStatut);

        // Boutons
        Button btnAjouter = new Button("➕ Ajouter");
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑️ Supprimer");
        Button btnVider = new Button("🔄 Vider");

        btnAjouter.getStyleClass().add("btn-success");
        btnModifier.getStyleClass().add("btn-primary");
        btnSupprimer.getStyleClass().add("btn-danger");
        btnVider.getStyleClass().add("btn-muted");

        HBox boutons = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnVider);
        boutons.setPadding(new Insets(10, 0, 0, 0));

        btnAjouter.setOnAction(e -> ajouterAbonnement());
        btnModifier.setOnAction(e -> modifierAbonnement());
        btnSupprimer.setOnAction(e -> supprimerAbonnement());
        btnVider.setOnAction(e -> viderFormulaire());

        VBox contenu = new VBox(10, grid, boutons);
        contenu.setPadding(new Insets(10));

        TitledPane pane = new TitledPane("📝 Formulaire Abonnement", contenu);
        Accordion accordion = new Accordion(pane);
        accordion.setExpandedPane(pane);
        return accordion;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 4 — Recherche
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private HBox buildRecherche() {
        tfRecherche.setPromptText("🔍 Rechercher par type d'offre...");
        tfRecherche.setPrefWidth(300);

        ComboBox<String> cbFiltre = new ComboBox<>();
        cbFiltre.getItems().addAll("Tous", "ACTIF", "EXPIRE", "SUSPENDU");
        cbFiltre.setValue("Tous");

        tfRecherche.textProperty().addListener((obs, o, newVal) -> filtrerAbonnements(newVal, cbFiltre.getValue()));

        cbFiltre.setOnAction(e -> filtrerAbonnements(tfRecherche.getText(), cbFiltre.getValue()));

        return new HBox(10,
                new Label("Recherche :"), tfRecherche,
                new Label("Statut :"), cbFiltre);
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 5 — TableView
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private TableView<Abonnement> buildTableView() {

        TableColumn<Abonnement, Integer> colId = new TableColumn<>("ID");
        TableColumn<Abonnement, String> colType = new TableColumn<>("Type Offre");
        TableColumn<Abonnement, Double> colPrix = new TableColumn<>("Prix/mois");
        TableColumn<Abonnement, Integer> colDuree = new TableColumn<>("Durée");
        TableColumn<Abonnement, String> colDebut = new TableColumn<>("Début");
        TableColumn<Abonnement, String> colFin = new TableColumn<>("Fin");
        TableColumn<Abonnement, String> colStatut = new TableColumn<>("Statut");
        TableColumn<Abonnement, Integer> colMembre = new TableColumn<>("Membre ID");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeOffre"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixMensuel"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeEngagement"));
        colDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colMembre.setCellValueFactory(new PropertyValueFactory<>("membreId"));

        tableView.getColumns().addAll(List.of(
                colId, colType, colPrix, colDuree,
                colDebut, colFin, colStatut, colMembre));
        tableView.setItems(abonnementList);
        tableView.setPrefHeight(300);

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, newVal) -> {
                    if (newVal != null)
                        remplirFormulaire(newVal);
                });

        return tableView;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 6 — Actions CRUD
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void ajouterAbonnement() {
        if (!validerFormulaire())
            return;
        Abonnement a = new Abonnement(
                cbTypeOffre.getValue(),
                slPrix.getValue(),
                spDuree.getValue(),
                dpDebut.getValue(),
                cbStatut.getValue(),
                cbMembre.getValue().getId());
        abonnementDAO.create(a);
        refreshTable();
        viderFormulaire();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement ajouté !");
    }

    private void modifierAbonnement() {
        if (abonnementSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez un abonnement !");
            return;
        }
        if (!validerFormulaire())
            return;
        abonnementSelectionne.setTypeOffre(cbTypeOffre.getValue());
        abonnementSelectionne.setPrixMensuel(slPrix.getValue());
        abonnementSelectionne.setDureeEngagement(spDuree.getValue());
        abonnementSelectionne.setDateDebut(dpDebut.getValue());
        abonnementSelectionne.setStatut(cbStatut.getValue());
        abonnementSelectionne.setMembreId(cbMembre.getValue().getId());
        abonnementDAO.update(abonnementSelectionne);
        refreshTable();
        viderFormulaire();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement modifié !");
    }

    private void supprimerAbonnement() {
        if (abonnementSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez un abonnement !");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer cet abonnement ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                abonnementDAO.delete(abonnementSelectionne.getId());
                refreshTable();
                viderFormulaire();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement supprimé !");
            }
        });
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 7 — Utilitaires
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void refreshTable() {
        abonnementList.clear();
        abonnementList.addAll(abonnementDAO.findAll());
    }

    private void remplirFormulaire(Abonnement a) {
        abonnementSelectionne = a;
        cbTypeOffre.setValue(a.getTypeOffre());
        spDuree.getValueFactory().setValue(a.getDureeEngagement());
        slPrix.setValue(a.getPrixMensuel());
        dpDebut.setValue(a.getDateDebut());
        cbStatut.setValue(a.getStatut());
        cbMembre.getItems().stream()
                .filter(m -> m.getId() == a.getMembreId())
                .findFirst()
                .ifPresent(cbMembre::setValue);
    }

    private void viderFormulaire() {
        abonnementSelectionne = null;
        cbTypeOffre.setValue(TypeOffre.CLASSIQUE);
        spDuree.getValueFactory().setValue(12);
        slPrix.setValue(290);
        dpDebut.setValue(null);
        cbStatut.setValue(StatutAbonnement.ACTIF);
        cbMembre.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validerFormulaire() {
        if (cbTypeOffre.getValue() == null || dpDebut.getValue() == null
                || cbMembre.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return false;
        }
        return true;
    }

    private void filtrerAbonnements(String recherche, String filtre) {
        abonnementList.clear();
        abonnementDAO.findAll().stream()
                .filter(a -> {
                    boolean matchRecherche = recherche.isEmpty()
                            || a.getTypeOffre().name().toLowerCase()
                                    .contains(recherche.toLowerCase());
                    boolean matchFiltre = filtre.equals("Tous")
                            || a.getStatut().name().equals(filtre);
                    return matchRecherche && matchFiltre;
                })
                .forEach(abonnementList::add);
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}