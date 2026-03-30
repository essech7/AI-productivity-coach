package com.productivitycoach.ui.controllers;

import com.productivitycoach.exception.AppException;
import com.productivitycoach.model.Categorie;
import com.productivitycoach.model.Tache;
import com.productivitycoach.service.TacheService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur du formulaire de création / modification d'une tâche.
 */
public class TacheFormController implements Initializable {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Categorie> categorieCombo;
    @FXML private ComboBox<Tache.Priorite> prioriteCombo;
    @FXML private DatePicker echeancePicker;
    @FXML private Spinner<Integer> tempsEstimeSpinner;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final TacheService service = new TacheService();
    private Tache tacheAModifier = null;  // null = création, sinon = édition

    // Catégories statiques (à charger depuis BD dans une version complète)
    private static final List<Categorie> CATEGORIES = List.of(
        new Categorie(1, "Études",    "#9b59b6", "book"),
        new Categorie(2, "Travail",   "#2ecc71", "briefcase"),
        new Categorie(3, "Personnel", "#e74c3c", "user")
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categorieCombo.setItems(FXCollections.observableArrayList(CATEGORIES));
        categorieCombo.setValue(CATEGORIES.get(0));

        prioriteCombo.setItems(FXCollections.observableArrayList(Tache.Priorite.values()));
        prioriteCombo.setValue(Tache.Priorite.MOYENNE);

        SpinnerValueFactory<Integer> svf =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 480, 30, 15);
        tempsEstimeSpinner.setValueFactory(svf);

        // Pré-remplir si modification
        if (tacheAModifier != null) {
            titreField.setText(tacheAModifier.getTitre());
            descriptionArea.setText(tacheAModifier.getDescription());
            categorieCombo.setValue(tacheAModifier.getCategorie());
            prioriteCombo.setValue(tacheAModifier.getPriorite());
            echeancePicker.setValue(tacheAModifier.getEcheance());
            tempsEstimeSpinner.getValueFactory().setValue(tacheAModifier.getTempsEstime());
        }
    }

    /** Appelé par le contrôleur parent pour passer la tâche à éditer. */
    public void setTache(Tache tache) {
        this.tacheAModifier = tache;
    }

    @FXML
    public void handleSave(ActionEvent event) {
        clearError();
        try {
            if (tacheAModifier == null) {
                // Création
                Tache nouvelle = new Tache(
                    titreField.getText().trim(),
                    categorieCombo.getValue(),
                    prioriteCombo.getValue(),
                    echeancePicker.getValue()
                );
                nouvelle.setDescription(descriptionArea.getText());
                nouvelle.setTempsEstime(tempsEstimeSpinner.getValue());
                service.creer(nouvelle);
            } else {
                // Modification
                tacheAModifier.setTitre(titreField.getText().trim());
                tacheAModifier.setDescription(descriptionArea.getText());
                tacheAModifier.setCategorie(categorieCombo.getValue());
                tacheAModifier.setPriorite(prioriteCombo.getValue());
                tacheAModifier.setEcheance(echeancePicker.getValue());
                tacheAModifier.setTempsEstime(tempsEstimeSpinner.getValue());
                service.modifier(tacheAModifier);
            }
            fermerFenetre();
        } catch (AppException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        fermerFenetre();
    }

    private void fermerFenetre() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}
