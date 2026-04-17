package com.coach.app.controllers;

import com.coach.app.ai.GeminiAiClient;
import com.coach.app.models.Task;
import com.coach.app.services.TaskManagementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class DashboardController {

    @FXML private ListView<Task> taskListView;
    @FXML private TextField taskTitleField;
    @FXML private TextArea taskDescField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private DatePicker deadlinePicker;
    
    // AI Assistant Tab
    @FXML private TextArea aiPromptArea;
    @FXML private TextArea aiResponseArea;

    private TaskManagementService taskService;
    private GeminiAiClient aiClient;
    private ObservableList<Task> observableTasks;

    @FXML
    public void initialize() {
        taskService = new TaskManagementService();
        aiClient = new GeminiAiClient();
        observableTasks = FXCollections.observableArrayList();
        
        categoryCombo.setItems(FXCollections.observableArrayList("Etudes", "Travail", "Personnel"));
        categoryCombo.getSelectionModel().selectFirst();

        taskListView.setItems(observableTasks);

        // Load tasks for a dummy user ID = 1
        loadTasks(1);
    }

    private void loadTasks(int userId) {
        observableTasks.clear();
        observableTasks.addAll(taskService.getUserTasks(userId));
    }

    @FXML
    private void handleAddTask() {
        Task newTask = new Task(
            0,
            taskTitleField.getText(),
            taskDescField.getText(),
            1, // User ID dummy
            categoryCombo.getValue(),
            "Moyenne", // Priorité par défaut
            deadlinePicker.getValue() != null ? deadlinePicker.getValue() : LocalDate.now().plusDays(1),
            "En attente"
        );
        
        if (taskService.createNewTask(newTask)) {
            loadTasks(1); // Refresh
            taskTitleField.clear();
            taskDescField.clear();
        } else {
            showAlert("Erreur", "Veuillez remplir le titre de la tâche.");
        }
    }

    @FXML
    private void handleAskAi() {
        String prompt = aiPromptArea.getText();
        if (prompt.trim().isEmpty()) {
            showAlert("Info", "Posez une question ou demandez de planifier une tâche !");
            return;
        }
        
        aiResponseArea.setText("L'IA réfléchit...");
        
        // Exécution dans un thread séparé pour ne pas bloquer l'interface
        new Thread(() -> {
            String response = aiClient.askAi(prompt);
            javafx.application.Platform.runLater(() -> aiResponseArea.setText(response));
        }).start();
    }

    @FXML
    private void handleBreakdown() {
        String taskName = taskTitleField.getText();
        if (taskName.trim().isEmpty()) {
            showAlert("Info", "Veuillez d'abord écrire un titre de tâche pour la découper !");
            return;
        }
        
        aiResponseArea.setText("Découpage en cours par l'IA...");
        
        new Thread(() -> {
            String response = aiClient.breakdownTask(taskName);
            javafx.application.Platform.runLater(() -> aiResponseArea.setText(response));
        }).start();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
