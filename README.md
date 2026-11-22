# KidsCamp ☀️

Application mobile Android de gestion des activités d’un centre de vacances pour enfants.

---

##  Présentation

**KidsCamp** permet de gérer les profils des enfants, de consulter les ateliers proposés, d’inscrire les enfants aux activités et de calculer automatiquement le budget familial. 

Ce projet a été réalisé dans le cadre du mini-projet du cours de développement mobile à l'ISSAT Sousse.

---

##  Objectifs

* **Simplifier** la gestion des enfants inscrits au centre.
* **Consulter** les ateliers disponibles selon leur catégorie.
* **Vérifier** strictement les règles d’inscription avant de réserver une activité.
* **Visualiser** le coût détaillé des activités et le total familial avec remises.

---

##  Fonctionnalités

* **Accueil :** Navigation fluide vers les différentes sections de l’application.
* **Gestion des enfants :** Ajout, consultation, modification et suppression d’un profil enfant.
* **Catalogue des ateliers :** Affichage des activités, recherche dynamique et filtrage par catégorie.
* **Détail d’un atelier :** Consultation complète des informations d’une activité (horaires, âge requis, places, tarif).
* **Inscriptions :** Inscription et annulation d’un enfant à un atelier.
* **Validations métier :** 
  * Contrôle de la tranche d’âge requise.
  * Prévention des inscriptions en doublon.
  * Vérification des places disponibles.
  * Détection des conflits horaires.
* **Budget familial :** Détail des tarifs par enfant, calcul du sous-total, application de la réduction fratrie et calcul du net à payer.
* **Sauvegarde locale :** Persistance des profils et des inscriptions directement sur l’appareil.

---

##  Technologies utilisées

* **Langage :** Kotlin
* **Plateforme :** Android
* **Interface utilisateur :** Layouts XML
* **IDE :** Android Studio
* **Stockage local :** `SharedPreferences` & sérialisation `JSON`
* **Données des ateliers :** Jeu de données défini localement dans l’application

> **Note :** L’application est conçue pour fonctionner 100 % hors ligne (localement), sans dépendre d’un serveur externe ou d’une API distante.

---

##  Prérequis

Pour ouvrir et exécuter ce projet, vous devez disposer de :
* **Android Studio** (version récente recommandée).
* Un **SDK Android** compatible avec la configuration du projet.
* Un **émulateur Android (AVD)** ou un **appareil physique Android** (avec le débogage USB activé) pour effectuer les tests.

*(Les versions exactes du SDK et les niveaux d'API minimums sont définis dans les fichiers `build.gradle` / `build.gradle.kts` du projet).*

---

##  Installation et lancement

1. **Cloner le dépôt** GitHub ou télécharger et extraire l'archive ZIP du projet :
   ```bash
   git clone https://github.com
   ```
2. Ouvrir **Android Studio**.
3. Cliquer sur **Open** et sélectionner le dossier racine du projet (contenant `settings.gradle` ou `settings.gradle.kts`).
4. Attendre la fin de la synchronisation **Gradle**.
5. Connecter un appareil Android ou démarrer un émulateur.
6. Cliquer sur le bouton **Run ▶** (ou appuyer sur `Shift + F10`) pour compiler et lancer l’application.

---

##  Plan de tests

Avant la livraison finale, les scénarios fonctionnels suivants doivent être validés :

| Scénario de test | Résultat attendu |
| :--- | :--- |
| Ajouter un enfant avec des informations valides | Le profil est créé et apparaît dans la liste. |
| Ajouter un enfant avec un prénom vide ou un âge invalide | L’application bloque la saisie et refuse les données incorrectes. |
| Inscrire un enfant à un atelier adapté à son âge | L’inscription est validée et enregistrée. |
| Inscrire un enfant à un atelier incompatible avec son âge | L’inscription est refusée avec un message explicatif. |
| Réinscrire le même enfant au même atelier | Le doublon est détecté et refusé. |
| Inscrire un enfant à un atelier complet ou en conflit horaire | L’inscription est refusée. |
| Fermer complètement puis relancer l’application | Les enfants et les inscriptions sauvegardés sont conservés. |
| Annuler une inscription existante | Le nombre de places disponibles et la facture sont mis à jour. |

*(Les résultats détaillés et captures de chaque test sont consignés dans le rapport technique).*

---

##  Organisation du projet

La structure principale du code source s'articule autour des composants suivants :

```text
app/src/main/
├── java/com/.../kidscamp/
│   ├── MainActivity.kt          # Écran d’accueil et menu principal
│   ├── ChildrenActivity.kt      # Gestion CRUD des profils des enfants
│   ├── WorkshopsActivity.kt     # Recherche, filtrage et inscriptions aux ateliers
│   ├── InvoiceActivity.kt       # Calcul détaillé du budget familial et réductions
│   ├── model/                   # Modèles de données (Child, Workshop, Registration)
│   └── data/
│       └── KidsCampData.kt      # Gestion des données partagées et sauvegarde locale
└── res/
    ├── layout/                  # Fichiers d'interfaces graphiques XML
    └── drawable/                # Ressources graphiques, icônes et arrière-plans
```

---

##  Données et confidentialité

* Toutes les données de démonstration sont stockées localement sur l’appareil via `SharedPreferences`.
* L’application ne nécessite aucune création de compte tiers ni connexion à un serveur distant.
* **Confidentialité :** Aucune donnée personnelle réelle n'est utilisée dans le jeu de données de test ni dans les captures d’écran.

---

##  Captures d’écran

*(Ajouter dans le dossier `captures/` des captures d'écran authentiques de la version finale exécutée sur émulateur ou appareil réel).*

| Écran d'accueil | Gestion des enfants | Catalogue des ateliers | Budget familial |
| :---: | :---: | :---: | :---: |
| ![Accueil](captures/accueil.png) | ![Enfants](captures/enfants.png) | ![Ateliers](captures/ateliers.png) | ![Facture](captures/budget.png) |

---

## 🤖 Déclaration des aides

Conformément aux consignes pédagogiques, l'ensemble des outils d’assistance utilisés durant le développement (y compris les outils d'intelligence artificielle) est déclaré en toute transparence dans le fichier [`DECLARATION_SOURCES.md`](DECLARATION_SOURCES.md). L'intégralité du code source et des règles métier est maîtrisée et explicable.

---

## 👩‍🎓 Informations sur le projet

* **Projet :** KidsCamp
* **Étudiante** :**Louhichi Nourelhouda
* **Établissement :** ISSAT Sousse
* **Cours :** Développement mobile
*


---
