package com.coach.app.models;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private int userId;
    private String category;
    private String priority;
    private LocalDate deadline;
    private String status;

    public Task() {}

    public Task(int id, String title, String description, int userId, String category, String priority, LocalDate deadline, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return title + " [" + category + "] - " + status;
    }
}
