package com.coach.app.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String preferences;
    private String goals;

    public User() {}

    public User(int id, String username, String password, String preferences, String goals) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.preferences = preferences;
        this.goals = goals;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    
    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }
}
