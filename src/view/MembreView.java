package view;

import controller.MembreController;
import model.Membre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class MembreView {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 1 — Controller & Données
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private MembreController membreController = new MembreController();
    private ObservableList<Membre> membreList = FXCollections.observableArrayList();
    private TableView<Membre> tableView = new TableView<>();

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 2 — Champs du formulaire
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private TextField tfNom = new TextField();
    private TextField tfPrenom = new TextField();
    private TextField tfEmail = new TextField();
    private TextField tfTelephone = new TextField();
    private DatePicker dpNaissance = new DatePicker();
    private CheckBox cbActif = new CheckBox("Membre actif");
    private TextField tfRecherche = new TextField();

    // Membre sélectionné pour modification
    private Membre membreSelectionne = null;

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // MÉTHODE PRINCIPALE — retourne la vue
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public VBox getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Titre
        Label titre = new Label("Gestion des Membres");
        titre.getStyleClass().add("page-title");

        // Assemblage
        root.getChildren().addAll(
                titre,
                buildFormulaire(),
                buildRecherche(),
                buildTableView());

        // Chargement initial
        refreshTable();

        return root;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 3 — Formulaire (Accordion)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private Accordion buildFormulaire() {

        // Tooltips
        tfNom.setTooltip(new Tooltip("Entrez le nom du membre"));
        tfPrenom.setTooltip(new Tooltip("Entrez le prénom du membre"));
        tfEmail.setTooltip(new Tooltip("Format : exemple@email.com"));
        tfTelephone.setTooltip(new Tooltip("Format : 06XXXXXXXX"));

        // Placeholders
        tfNom.setPromptText("Nom");
        tfPrenom.setPromptText("Prénom");
        tfEmail.setPromptText("email@exemple.com");
        tfTelephone.setPromptText("0612345678");
        cbActif.setSelected(true);

        // GridPane pour aligner les champs
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("Nom :"), tfNom, new Label("Prénom :"), tfPrenom);
        grid.addRow(1, new Label("Email :"), tfEmail, new Label("Téléphone :"), tfTelephone);
        grid.addRow(2, new Label("Naissance :"), dpNaissance, cbActif);

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

        VBox contenu = new VBox(10, grid, boutons);
        contenu.setPadding(new Insets(10));

        // Actions boutons
        btnAjouter.setOnAction(e -> ajouterMembre());
        btnModifier.setOnAction(e -> modifierMembre());
        btnSupprimer.setOnAction(e -> supprimerMembre());
        btnVider.setOnAction(e -> viderFormulaire());

        // Accordion + TitledPane
        TitledPane pane = new TitledPane("📝 Formulaire Membre", contenu);
        Accordion accordion = new Accordion(pane);
        accordion.setExpandedPane(pane);

        return accordion;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 4 — Barre de recherche
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private HBox buildRecherche() {
        tfRecherche.setPromptText("🔍 Rechercher par nom ou prénom...");
        tfRecherche.setPrefWidth(300);

        ComboBox<String> cbFiltre = new ComboBox<>();
        cbFiltre.getItems().addAll("Tous", "Actifs", "Inactifs");
        cbFiltre.setValue("Tous");

        // Recherche en temps réel
        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerMembres(newVal, cbFiltre.getValue());
        });

        cbFiltre.setOnAction(e -> {
            filtrerMembres(tfRecherche.getText(), cbFiltre.getValue());
        });

        HBox barre = new HBox(10, new Label("Recherche :"), tfRecherche,
                new Label("Filtre :"), cbFiltre);
        barre.setPadding(new Insets(5, 0, 5, 0));
        return barre;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 5 — TableView
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private TableView<Membre> buildTableView() {

        TableColumn<Membre, Integer> colId = new TableColumn<>("ID");
        TableColumn<Membre, String> colNom = new TableColumn<>("Nom");
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        TableColumn<Membre, String> colEmail = new TableColumn<>("Email");
        TableColumn<Membre, String> colTel = new TableColumn<>("Téléphone");
        TableColumn<Membre, String> colDob = new TableColumn<>("Naissance");
        TableColumn<Membre, Boolean> colActif = new TableColumn<>("Actif");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        colId.setPrefWidth(40);
        colNom.setPrefWidth(120);
        colPrenom.setPrefWidth(120);
        colEmail.setPrefWidth(180);
        colTel.setPrefWidth(120);
        colDob.setPrefWidth(100);
        colActif.setPrefWidth(60);

        tableView.getColumns().addAll(List.of(
                colId, colNom, colPrenom, colEmail, colTel, colDob, colActif));
        tableView.setItems(membreList);
        tableView.setPrefHeight(300);

        // Clic sur une ligne → remplit le formulaire
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null)
                        remplirFormulaire(newVal);
                });

        return tableView;
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 6 — Actions CRUD
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void ajouterMembre() {
        if (!validerFormulaire())
            return;
        Membre m = new Membre(
                tfNom.getText(), tfPrenom.getText(),
                tfEmail.getText(), tfTelephone.getText(),
                dpNaissance.getValue(), cbActif.isSelected());
        membreController.create(m);
        refreshTable();
        viderFormulaire();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre ajouté !");
    }

    private void modifierMembre() {
        if (membreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez un membre !");
            return;
        }
        if (!validerFormulaire())
            return;
        membreSelectionne.setNom(tfNom.getText());
        membreSelectionne.setPrenom(tfPrenom.getText());
        membreSelectionne.setEmail(tfEmail.getText());
        membreSelectionne.setTelephone(tfTelephone.getText());
        membreSelectionne.setDateNaissance(dpNaissance.getValue());
        membreSelectionne.setActif(cbActif.isSelected());
        membreController.update(membreSelectionne);
        refreshTable();
        viderFormulaire();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre modifié !");
    }

    private void supprimerMembre() {
        if (membreSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez un membre !");
            return;
        }
        // Confirmation avant suppression
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + membreSelectionne + " ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                membreController.delete(membreSelectionne.getId());
                refreshTable();
                viderFormulaire();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Membre supprimé !");
            }
        });
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // BLOC 7 — Utilitaires
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    private void refreshTable() {
        membreList.clear();
        membreList.addAll(membreController.findAll());
    }

    private void remplirFormulaire(Membre m) {
        membreSelectionne = m;
        tfNom.setText(m.getNom());
        tfPrenom.setText(m.getPrenom());
        tfEmail.setText(m.getEmail());
        tfTelephone.setText(m.getTelephone());
        dpNaissance.setValue(m.getDateNaissance());
        cbActif.setSelected(m.isActif());
    }

    private void viderFormulaire() {
        membreSelectionne = null;
        tfNom.clear();
        tfPrenom.clear();
        tfEmail.clear();
        tfTelephone.clear();
        dpNaissance.setValue(null);
        cbActif.setSelected(true);
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validerFormulaire() {
        if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty()
                || tfEmail.getText().isEmpty() || tfTelephone.getText().isEmpty()
                || dpNaissance.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return false;
        }
        return true;
    }

    private void filtrerMembres(String recherche, String filtre) {
        membreList.clear();
        membreList.addAll(membreController.filtrerMembres(recherche, filtre));
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}