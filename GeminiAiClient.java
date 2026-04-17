package com.coach.app.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAiClient {
    // Remplacer par votre clé API
    private static final String API_KEY = "VOTRE_CLE_API_ICI";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private HttpClient client;
    private Gson gson;

    public GeminiAiClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public String askAi(String promptText) {
        if ("VOTRE_CLE_API_ICI".equals(API_KEY)) {
            return "Veuillez configurer votre clé API Gemini dans GeminiAiClient.java";
        }

        try {
            // Construire le payload JSON pour Gemini
            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            
            part.addProperty("text", promptText);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            root.add("contents", contents);

            String requestBody = gson.toJson(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parsez la réponse de Gemini
             if (response.statusCode() == 200) {
                 JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                 return jsonResponse.getAsJsonArray("candidates")
                         .get(0).getAsJsonObject()
                         .getAsJsonObject("content")
                         .getAsJsonArray("parts")
                         .get(0).getAsJsonObject()
                         .get("text").getAsString();
             } else {
                 return "Erreur API : " + response.statusCode() + " - " + response.body();
             }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec l'IA.";
        }
    }

    public String reformulateGoal(String vagueGoal) {
        String prompt = "Tu es un coach de productivité. Reformule cet objectif flou en un objectif SMART (Spécifique, Mesurable, Atteignable, Réaliste, Temporel) : " + vagueGoal;
        return askAi(prompt);
    }

    public String breakdownTask(String taskToBreakdown) {
        String prompt = "Tu es un coach de productivité. Découpe cette grande tâche en sous-tâches réalisables étape par étape sous forme de liste à puces : " + taskToBreakdown;
        return askAi(prompt);
    }
}
