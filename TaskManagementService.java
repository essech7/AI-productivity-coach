package com.coach.app.services;

import com.coach.app.dao.TaskDAO;
import com.coach.app.models.Task;

import java.util.List;

public class TaskManagementService {
    private TaskDAO taskDAO;

    public TaskManagementService() {
        this.taskDAO = new TaskDAO();
    }

    public List<Task> getUserTasks(int userId) {
        return taskDAO.getTasksByUser(userId);
    }

    public boolean createNewTask(Task task) {
        // Logique métier : validation simple
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return false;
        }
        return taskDAO.addTask(task);
    }

    public boolean completeTask(int taskId) {
        return taskDAO.updateTaskStatus(taskId, "Terminée");
    }
    
    // Calcul de la productivité simple (taux de complétion)
    public double calculateCompletionRate(int userId) {
        List<Task> tasks = getUserTasks(userId);
        if (tasks.isEmpty()) return 0.0;

        long completed = tasks.stream()
                .filter(t -> "Terminée".equalsIgnoreCase(t.getStatus()))
                .count();

        return (double) completed / tasks.size() * 100;
    }
}
