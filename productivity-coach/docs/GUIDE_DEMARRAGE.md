# 🚀 Guide de démarrage — AI Productivity Coach

## Vue d'ensemble du projet

| Info | Détail |
|------|--------|
| **Langage** | Java 17 |
| **Interface** | JavaFX 21 |
| **Base de données** | MySQL 8+ (ou PostgreSQL 14+) |
| **IA** | Google Gemini 2.0 Flash |
| **Build** | Maven |
| **IDE recommandé** | IntelliJ IDEA |

---

## Étape 1 — Prérequis logiciels

### 1.1 Installer Java 17
Téléchargez **JDK 17** (LTS) depuis [https://adoptium.net](https://adoptium.net)

Vérifier l'installation :
```bash
java -version
# → openjdk version "17.x.x"
```

### 1.2 Installer Maven
Téléchargez Maven depuis [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

Vérifier :
```bash
mvn -version
# → Apache Maven 3.x.x
```

### 1.3 Installer MySQL
Téléchargez MySQL 8+ depuis [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)

Ou avec XAMPP : [https://www.apachefriends.org](https://www.apachefriends.org)

### 1.4 Installer IntelliJ IDEA
Version Community (gratuite) : [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)

---

## Étape 2 — Obtenir une clé API Gemini (GRATUIT)

1. Allez sur **[https://aistudio.google.com/projects](https://aistudio.google.com/projects)**
2. Connectez-vous avec un compte Google
3. Cliquez sur **"Get API Key"** → **"Create API Key"**
4. Copiez la clé générée (commence par `AIza...`)

---

## Étape 3 — Configurer la base de données

### 3.1 Démarrer MySQL

Via XAMPP : démarrez le module MySQL dans le panneau de contrôle.

Via terminal :
```bash
mysql -u root -p
```

### 3.2 Créer la base de données

Exécutez le script fourni dans `docs/schema.sql` :

```bash
mysql -u root -p < docs/schema.sql
```

Ou via MySQL Workbench / phpMyAdmin : ouvrez le fichier `docs/schema.sql` et exécutez-le.

### 3.3 Vérifier
```sql
USE productivity_coach;
SHOW TABLES;
-- Doit afficher : utilisateurs, categories, taches, sous_taches,
--                 sessions_travail, suggestions_ia
```

---

## Étape 4 — Configurer le projet Java

### 4.1 Clé API Gemini

Ouvrez le fichier :
```
src/main/java/com/productivitycoach/service/GeminiService.java
```

Remplacez à la ligne 18 :
```java
private static final String GEMINI_API_KEY = "VOTRE_CLE_API_ICI";
// ↓ remplacer par :
private static final String GEMINI_API_KEY = "AIzaSy...votre_vraie_clé...";
```

### 4.2 Connexion à la base de données

Ouvrez :
```
src/main/java/com/productivitycoach/util/DatabaseConnection.java
```

Modifiez les lignes 20-22 :
```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/productivity_coach";
private static final String DB_USER     = "root";
private static final String DB_PASSWORD = "votre_mot_de_passe_mysql";
```

---

## Étape 5 — Ouvrir dans IntelliJ IDEA

1. **File → Open** → sélectionnez le dossier `productivity-coach/`
2. IntelliJ détecte automatiquement le `pom.xml` → cliquez **"Trust Project"**
3. Maven télécharge automatiquement toutes les dépendances (attendez la fin)
4. Vérifiez que **JDK 17** est sélectionné : *File → Project Structure → SDK*

---

## Étape 6 — Lancer l'application

### Via IntelliJ :
- Ouvrez `Main.java`
- Clic droit → **Run 'Main'**

### Via Maven en terminal :
```bash
mvn javafx:run
```

---

## Structure des fichiers

```
productivity-coach/
├── pom.xml                              ← Dépendances Maven
├── docs/
│   ├── schema.sql                       ← Script base de données
│   ├── uml_cas_utilisation.puml         ← Diagramme UML cas d'utilisation
│   └── uml_classes.puml                 ← Diagramme UML classes
└── src/main/
    ├── java/com/productivitycoach/
    │   ├── Main.java                     ← Point d'entrée
    │   ├── model/                        ← Couche modèle (entités)
    │   │   ├── Utilisateur.java
    │   │   ├── Tache.java
    │   │   ├── Categorie.java
    │   │   └── SousTache.java
    │   ├── dao/                          ← Couche persistance (JDBC)
    │   │   ├── DAO.java                  ← Interface générique
    │   │   ├── DAOException.java         ← Exception personnalisée
    │   │   ├── UtilisateurDAO.java
    │   │   └── TacheDAO.java
    │   ├── service/                      ← Couche métier
    │   │   ├── UtilisateurService.java
    │   │   ├── TacheService.java
    │   │   └── GeminiService.java        ← Intégration IA
    │   ├── ui/controllers/               ← Contrôleurs JavaFX
    │   │   ├── LoginController.java
    │   │   ├── RegisterController.java
    │   │   ├── DashboardController.java
    │   │   └── TacheFormController.java
    │   ├── util/
    │   │   └── DatabaseConnection.java   ← Singleton JDBC
    │   └── exception/
    │       └── AppException.java
    └── resources/
        ├── fxml/                         ← Vues JavaFX
        │   ├── LoginView.fxml
        │   ├── RegisterView.fxml
        │   ├── DashboardView.fxml
        │   └── TacheFormView.fxml
        └── css/
            └── app.css                   ← Styles (thème sombre)
```

---

## Architecture en couches

```
┌─────────────────────────────────────────────┐
│              COUCHE UI (JavaFX)             │
│    Controllers ←→ FXML Views ←→ CSS        │
├─────────────────────────────────────────────┤
│           COUCHE SERVICE (Métier)           │
│   Validation · Logique · Session · IA       │
├─────────────────────────────────────────────┤
│          COUCHE DAO (Persistance)           │
│   Interface DAO<T> + implémentations JDBC   │
├─────────────────────────────────────────────┤
│              BASE DE DONNÉES                │
│            MySQL / PostgreSQL               │
└─────────────────────────────────────────────┘
```

---

## Concepts du cours appliqués

### Chapitre 1 — Classes abstraites & Interfaces
| Concept | Où dans le projet |
|---------|-------------------|
| **Interface** | `DAO<T>` — contrat CRUD générique |
| **Implémentation** | `UtilisateurDAO`, `TacheDAO` implémentent `DAO<T>` |
| **Polymorphisme** | Les services utilisent `DAO<T>` sans connaître l'implémentation |

### Chapitre 2 — Exceptions
| Concept | Où dans le projet |
|---------|-------------------|
| **Exception personnalisée** | `DAOException`, `AppException` |
| **Propagation** | Les DAO lancent `DAOException`, les services la catchent et lancent `AppException` |
| **Hiérarchie** | `AppException extends RuntimeException` |

### Chapitre 3 — API JDBC
| Concept | Où dans le projet |
|---------|-------------------|
| **`DriverManager.getConnection()`** | `DatabaseConnection.java` |
| **`PreparedStatement`** | Toutes les méthodes DAO (protection injection SQL) |
| **`ResultSet`** | Méthodes `map()` dans chaque DAO |
| **Pattern Singleton** | `DatabaseConnection` — une seule connexion |
| **`Statement.RETURN_GENERATED_KEYS`** | Récupération des ID auto-générés |

---

## Répartition suggérée du travail

### Binôme
| Membre 1 | Membre 2 |
|----------|----------|
| Modèles + DAO (persistance) | Services + Contrôleurs (métier + UI) |
| `schema.sql` + `DatabaseConnection` | Intégration API Gemini |
| Diagramme UML classes | Diagramme UML cas d'utilisation |
| Vue Dashboard | Vues Login / Register / Formulaire |

---

## Générer les diagrammes UML

Rendez-vous sur **[https://www.plantuml.com/plantuml/uml/](https://www.plantuml.com/plantuml/uml/)**

1. Ouvrez le fichier `.puml` voulu depuis le dossier `docs/`
2. Copiez son contenu dans l'éditeur en ligne
3. Le diagramme s'affiche instantanément
4. Exportez en PNG pour votre rapport

---

## Dépannage courant

| Problème | Solution |
|----------|----------|
| `Communications link failure` | MySQL n'est pas démarré → lancer XAMPP |
| `Access denied for user 'root'` | Vérifiez le mot de passe dans `DatabaseConnection.java` |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Maven n'a pas téléchargé les dépendances → `mvn clean install` |
| `Erreur API Gemini 400` | Vérifiez votre clé API dans `GeminiService.java` |
| `JavaFX runtime components are missing` | Utilisez `mvn javafx:run` au lieu de `java Main` directement |

---

## Ressources utiles

- 📘 [Documentation JavaFX](https://openjfx.io/javadoc/21/)
- 🎨 [PlantUML en ligne](https://www.plantuml.com/plantuml/uml/)
- 🤖 [Documentation Gemini API](https://ai.google.dev/gemini-api/docs)
- 🔑 [Google AI Studio (clé API)](https://aistudio.google.com/projects)
- 🗄️ [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
