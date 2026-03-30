package com.productivitycoach.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.productivitycoach.exception.AppException;
import okhttp3.*;

import java.io.IOException;

/**
 * Service d'intégration avec l'API Gemini (Google AI).
 *
 * Fonctionnalités IA offertes :
 *   1. Reformuler un objectif flou en objectif SMART
 *   2. Proposer des priorités pour une liste de tâches
 *   3. Découper une grosse tâche en sous-tâches
 *   4. Générer un plan de révision personnalisé
 *
 * CONFIGURATION : remplacez GEMINI_API_KEY par votre clé
 *   → https://aistudio.google.com/projects
 */
public class GeminiService {

    private static final String GEMINI_API_KEY = "VOTRE_CLE_API_ICI";
    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/"
        + "gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

    private final OkHttpClient httpClient;
    private final Gson gson;

    public GeminiService() {
        this.httpClient = new OkHttpClient();
        this.gson       = new Gson();
    }

    // ── API publique ───────────────────────────────────────────────────────────

    /**
     * Reformule un objectif vague en objectif clair et actionnable.
     * Exemple : "Préparer examen" → objectif SMART détaillé
     */
    public String reformulerObjectif(String objectifFlou) {
        String prompt = String.format("""
            Tu es un coach de productivité expert. L'utilisateur a écrit cet objectif :
            "%s"
            
            Reformule-le en un objectif SMART (Spécifique, Mesurable, Atteignable,
            Réaliste, Temporel) en français. Sois concis et pratique.
            """, objectifFlou);
        return appelerGemini(prompt);
    }

    /**
     * Découpe une tâche complexe en liste de sous-tâches.
     * Exemple : "Préparer examen Java" → plan sur 7 jours
     */
    public String decomposerTache(String titreTache, String description, int joursDisponibles) {
        String prompt = String.format("""
            Tu es un assistant de planification. Découpe cette tâche en sous-tâches
            concrètes et réalisables sur %d jours :
            
            Tâche : %s
            Détails : %s
            
            Réponds avec une liste numérotée de sous-tâches, une par ligne.
            Chaque sous-tâche doit être courte (max 10 mots) et actionnable.
            Réponds uniquement en français.
            """, joursDisponibles, titreTache, description);
        return appelerGemini(prompt);
    }

    /**
     * Analyse la liste de tâches et propose un ordre de priorité.
     */
    public String proposerPriorites(String listeTaches, String contexteUtilisateur) {
        String prompt = String.format("""
            Tu es un expert en gestion du temps. Voici les tâches d'un étudiant/professionnel :
            
            %s
            
            Contexte : %s
            
            Propose un ordre de priorité avec une courte justification pour chaque tâche.
            Sois direct et pratique. Réponds en français.
            """, listeTaches, contexteUtilisateur);
        return appelerGemini(prompt);
    }

    /**
     * Génère des conseils de planification personnalisés.
     */
    public String genererConseils(String statsUtilisateur, String rythmeTravail) {
        String prompt = String.format("""
            Tu es un coach de productivité. Voici les statistiques de l'utilisateur :
            %s
            Son rythme préféré : %s
            
            Donne 3 conseils personnalisés et actionables pour améliorer sa productivité.
            Chaque conseil doit faire max 2 phrases. Réponds en français.
            """, statsUtilisateur, rythmeTravail);
        return appelerGemini(prompt);
    }

    // ── Appel HTTP à l'API Gemini ──────────────────────────────────────────────

    private String appelerGemini(String prompt) {
        // Construction du body JSON
        JsonObject body = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        body.add("contents", contents);

        RequestBody requestBody = RequestBody.create(
            gson.toJson(body),
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(GEMINI_URL)
            .post(requestBody)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new AppException("Erreur API Gemini : " + response.code());
            }
            String responseBody = response.body().string();
            return extraireTexte(responseBody);
        } catch (IOException e) {
            throw new AppException("Impossible de contacter l'API Gemini : "
                                   + e.getMessage(), e);
        }
    }

    /**
     * Extrait le texte de la réponse JSON Gemini.
     */
    private String extraireTexte(String json) {
        try {
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            return obj.getAsJsonArray("candidates")
                      .get(0).getAsJsonObject()
                      .getAsJsonObject("content")
                      .getAsJsonArray("parts")
                      .get(0).getAsJsonObject()
                      .get("text").getAsString();
        } catch (Exception e) {
            throw new AppException("Erreur de parsing réponse Gemini.", e);
        }
    }
}
