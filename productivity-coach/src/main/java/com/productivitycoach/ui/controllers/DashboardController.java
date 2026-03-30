package com.productivitycoach.ui.controllers;

import com.productivitycoach.exception.AppException;
import com.productivitycoach.model.Tache;
import com.productivitycoach.service.GeminiService;
import com.productivitycoach.service.TacheService;
import com.productivitycoach.service.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur principal du tableau de bord.
 * Affiche les tâches, statistiques et permet les interactions IA.
 */
public class DashboardController implements Initializable {

    // ── Statistiques ──────────────────────────────────────────────────────────
    @FXML private Label labelBienvenue;
    @FXML private Label labelTotalTaches;
    @FXML private Label labelTerminees;
    @FXML private Label labelTauxCompletion;
    @FXML private ProgressBar progressCompletion;

    // ── Tableau des tâches ────────────────────────────────────────────────────
    @FXML private TableView<Tache> tableTaches;
    @FXML private TableColumn<Tache, String> colTitre;
    @FXML private TableColumn<Tache, String> colCategorie;
    @FXML private TableColumn<Tache, String> colPriorite;
    @FXML private TableColumn<Tache, String> colEtat;
    @FXML private TableColumn<Tache, String> colEcheance;

    // ── Filtres ───────────────────────────────────────────────────────────────
    @FXML private ComboBox<String> comboFiltreEtat;
    @FXML private ComboBox<String> comboFiltrePriorite;

    // ── IA ────────────────────────────────────────────────────────────────────
    @FXML private TextArea iaInputArea;
    @FXML private TextArea iaOutputArea;
    @FXML private ComboBox<String> comboTypeIA;
    @FXML private Button btnDemanderIA;
    @FXML private ProgressIndicator progressIA;

    private final TacheService tacheService       = new TacheService();
    private final UtilisateurService userService  = new UtilisateurService();
    private final GeminiService geminiService     = new GeminiService();
    private ObservableList<Tache> tachesObservable;

    // ── Initialisation ────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurerTableau();
        configurerFiltres();
        configurerIA();
        rafraichir();
    }

    private void configurerTableau() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCategorie().getNom()));
        colPriorite.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPriorite().getLibelle()));
        colEtat.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEtat().getLibelle()));
        colEcheance.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEcheance() != null
                    ? c.getValue().getEcheance().toString() : "—"));

        // Colorier les lignes selon l'état
        tableTaches.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (tache == null || empty) {
                    setStyle("");
                } else if (tache.getEtat() == Tache.Etat.TERMINEE) {
                    setStyle("-fx-background-color: #e8f5e9;");
                } else if (tache.isEnRetard()) {
                    setStyle("-fx-background-color: #ffebee;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void configurerFiltres() {
        comboFiltreEtat.setItems(FXCollections.observableArrayList(
            "Tous", "À faire", "En cours", "Terminée", "Annulée"
        ));
        comboFiltreEtat.setValue("Tous");
        comboFiltreEtat.setOnAction(e -> appliquerFiltres());

        comboFiltrePriorite.setItems(FXCollections.observableArrayList(
            "Toutes", "Urgente", "Haute", "Moyenne", "Basse"
        ));
        comboFiltrePriorite.setValue("Toutes");
        comboFiltrePriorite.setOnAction(e -> appliquerFiltres());
    }

    private void configurerIA() {
        comboTypeIA.setItems(FXCollections.observableArrayList(
            "Reformuler un objectif",
            "Découper une tâche en sous-tâches",
            "Proposer des priorités",
            "Conseils personnalisés"
        ));
        comboTypeIA.setValue("Reformuler un objectif");
        progressIA.setVisible(false);
    }

    // ── Chargement des données ────────────────────────────────────────────────

    private void rafraichir() {
        try {
            List<Tache> taches = tacheService.getMesTaches();
            tachesObservable = FXCollections.observableArrayList(taches);
            tableTaches.setItems(tachesObservable);

            // Statistiques
            var utilisateur = UtilisateurService.getUtilisateurConnecte();
            labelBienvenue.setText("Bonjour, " + utilisateur.getNomComplet() + " 👋");
            labelTotalTaches.setText(String.valueOf(taches.size()));

            long terminees = taches.stream()
                .filter(t -> t.getEtat() == Tache.Etat.TERMINEE).count();
            labelTerminees.setText(String.valueOf(terminees));

            double taux = tacheService.getTauxCompletion();
            labelTauxCompletion.setText(String.format("%.0f%%", taux));
            progressCompletion.setProgress(taux / 100.0);

        } catch (AppException e) {
            showAlert("Erreur chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void appliquerFiltres() {
        if (tachesObservable == null) return;
        String filtreEtat     = comboFiltreEtat.getValue();
        String filtrePriorite = comboFiltrePriorite.getValue();

        List<Tache> filtrees = tachesObservable.stream()
            .filter(t -> filtreEtat.equals("Tous")
                || t.getEtat().getLibelle().equals(filtreEtat))
            .filter(t -> filtrePriorite.equals("Toutes")
                || t.getPriorite().getLibelle().equalsIgnoreCase(filtrePriorite))
            .collect(Collectors.toList());

        tableTaches.setItems(FXCollections.observableArrayList(filtrees));
    }

    // ── Actions tâches ────────────────────────────────────────────────────────

    @FXML
    public void handleNouvelleTache(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/TacheFormView.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nouvelle tâche");
            dialog.setScene(new Scene(root, 600, 500));
            dialog.showAndWait();
            rafraichir();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleModifierTache(ActionEvent event) {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner une tâche à modifier.",
                Alert.AlertType.WARNING);
            return;
        }
        // TODO: passer la tâche sélectionnée au contrôleur du formulaire
        handleNouvelleTache(event);
    }

    @FXML
    public void handleSupprimerTache(ActionEvent event) {
        Tache selected = tableTaches.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection", "Veuillez sélectionner une tâche.",
                Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer la tâche « " + selected.getTitre() + " » ?",
            ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                tacheService.supprimer(selected.getId());
                rafraichir();
            }
        });
    }

    // ── Actions IA ────────────────────────────────────────────────────────────

    @FXML
    public void handleDemanderIA(ActionEvent event) {
        String input = iaInputArea.getText().trim();
        if (input.isBlank()) {
            showAlert("IA", "Veuillez saisir une demande.", Alert.AlertType.WARNING);
            return;
        }
        progressIA.setVisible(true);
        btnDemanderIA.setDisable(true);
        iaOutputArea.setText("Consultation de l'IA en cours...");

        // Appel asynchrone pour ne pas bloquer l'UI JavaFX
        new Thread(() -> {
            try {
                String reponse = switch (comboTypeIA.getValue()) {
                    case "Reformuler un objectif"          -> geminiService.reformulerObjectif(input);
                    case "Découper une tâche en sous-tâches" -> geminiService.decomposerTache(input, "", 7);
                    case "Proposer des priorités"          -> geminiService.proposerPriorites(input, "");
                    default                                -> geminiService.genererConseils(input, "");
                };
                javafx.application.Platform.runLater(() -> {
                    iaOutputArea.setText(reponse);
                    progressIA.setVisible(false);
                    btnDemanderIA.setDisable(false);
                });
            } catch (AppException e) {
                javafx.application.Platform.runLater(() -> {
                    iaOutputArea.setText("❌ Erreur IA : " + e.getMessage());
                    progressIA.setVisible(false);
                    btnDemanderIA.setDisable(false);
                });
            }
        }).start();
    }

    // ── Déconnexion ───────────────────────────────────────────────────────────

    @FXML
    public void handleDeconnexion(ActionEvent event) {
        userService.deconnecter();
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) labelBienvenue.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("AI Productivity Coach");
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private void showAlert(String titre, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
