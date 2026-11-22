<div align="center">

#  KidsCamp — Gestion d'Ateliers & Budget Familial

**Application mobile Android native dédiée à la gestion des inscriptions d'un centre de vacances pour enfants.**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2F%20Local--First-0288D1?style=for-the-badge)](#%EF%B8%8F-architecture-et-organisation-du-projet)
[![Offline](https://img.shields.io/badge/Mode-100%25%20Hors--Ligne-FF6F00?style=for-the-badge)](#-stockage-local-et-confidentialité)

*Projet réalisé dans le cadre du module **Développement Mobile** — **ISSAT Sousse***

</div>

---

## 📋 Table des matières

- [Présentation](#-présentation)
- [Aperçu de l'application](#-aperçu-de-lapplication)
- [Fonctionnalités et règles métier](#-fonctionnalités-et-règles-métier)
- [Stack technique](#-stack-technique)
- [Architecture et organisation du projet](#%EF%B8%8F-architecture-et-organisation-du-projet)
- [Installation et lancement](#-installation-et-lancement)
- [Jeu de données et scénarios de test](#-jeu-de-données-et-scénarios-de-test)
- [Limitations connues et perspectives](#-limitations-connues-et-perspectives)
- [Transparence et confidentialité](#-transparence-et-confidentialité)
- [Auteure](#-auteure)

---

## 🎯 Présentation

**KidsCamp** est une application mobile Android conçue pour simplifier l'organisation quotidienne des parents et animateurs d'un centre de vacances. Elle centralise la gestion des profils des enfants, la découverte des ateliers créatifs, sportifs et scientifiques, le contrôle automatique des contraintes d'inscription et le calcul instantané du budget familial.

### Objectifs clés
- **Centraliser** les profils des enfants (CRUD complet avec persistance locale).
- **Faciliter la recherche** d'ateliers par mots-clés et par catégories (*Créativité*, *Sport*, *Sciences*).
- **Sécuriser les inscriptions** grâce à un moteur de validation métier strict (âge, capacité, doublons, conflits horaires).
- **Automatiser la facturation** avec calcul détaillé par enfant et application dynamique des remises fratrie.

---

## 📱 Aperçu de l'application

| Écran d'accueil & Navigation | Gestion des Enfants (CRUD) | Catalogue & Inscriptions | Budget & Facturation |
| :---: | :---: | :---: | :---: |
| <img src="captures/screen_home.png" width="200" alt="Accueil"/> | <img src="captures/screen_children.png" width="200" alt="Enfants"/> | <img src="captures/screen_workshops.png" width="200" alt="Ateliers"/> | <img src="captures/screen_invoice.png" width="200" alt="Budget"/> |

> *💡 Les captures d'écran authentiques sont disponibles dans le dossier [`captures/`](captures/).*

---

## ✨ Fonctionnalités et règles métier

### 1. Gestion des petits campeurs (`ChildrenActivity`)
- **Ajout, modification et suppression (CRUD)** des profils enfants (prénom, âge).
- **Contrôles de saisie intégrés :** blocage des champs vides, validation de longueur minimale (≥ 2 caractères) et restriction de la tranche d'âge autorisée (**3 à 17 ans**).
- **Intégrité des données :** la suppression d'un profil nettoie automatiquement ses inscriptions associées.

### 2. Catalogue interactif & Inscriptions (`WorkshopsActivity`)
- **Recherche dynamique en temps réel** (par titre d'atelier, catégorie ou jour) combinée à un **filtrage par catégorie**.
- **Moteur de validation métier (4 règles strictes) :**
  1. **Compatibilité d'âge :** vérifie que l'âge de l'enfant se situe dans l'intervalle `[minAge..maxAge]` de l'atelier.
  2. **Anti-doublon :** empêche d'inscrire deux fois le même enfant à une même activité.
  3. **Gestion de capacité :** bloque l'inscription lorsque le quota de places disponibles atteint `0`.
  4. **Détection de conflits horaires :** empêche toute inscription si l'enfant participe déjà à un autre atelier sur le même créneau horaire et le même jour.
- **Annulation d'inscription :** libère immédiatement une place et met à jour le récapitulatif financier.

### 3. Calcul automatique du budget familial (`InvoiceActivity`)
- Ventilation détaillée des coûts par enfant et par atelier réservé.
- Calcul automatique du **sous-total**, application de la **réduction fratrie** selon le nombre d'enfants inscrits et affichage du **net à payer (en DT)**.

---

## 🛠️ Stack technique

| Composant | Technologie / Outil | Rôle dans le projet |
| :--- | :--- | :--- |
| **Langage** | Kotlin | Logique applicative, règles métier et gestion des événements |
| **SDK & UI** | Android SDK / XML Layouts | Interfaces tactiles responsives et composants natifs (`AppCompat`) |
| **Persistance** | `SharedPreferences` + JSON | Sauvegarde locale légère et sérialisation des listes |
| **IDE & Build** | Android Studio / Gradle | Compilation, gestion des dépendances et tests sur émulateur/appareil |

> **Architecture 100 % Local-First :** L'application ne requiert aucune connexion Internet, aucun serveur externe et aucune clé API, garantissant une disponibilité totale et des temps de réponse instantanés.

---

## 🗂️ Architecture et organisation du projet

```text
app/src/main/
├── java/com/example/kidscamp/
│   ├── MainActivity.kt          # Point d'entrée et navigation principale
│   ├── ChildrenActivity.kt      # Gestion CRUD des profils enfants + validations
│   ├── WorkshopsActivity.kt     # Recherche, filtrage et moteur d'inscription métier
│   ├── InvoiceActivity.kt       # Calcul du budget familial, sous-total et remises
│   ├── model/
│   │   ├── Child.kt             # Modèle de données : Enfant (id, firstName, age)
│   │   ├── Workshop.kt          # Modèle de données : Atelier (horaires, âge, capacité, prix)
│   │   └── Registration.kt      # Modèle associatif : Inscription (childId, workshopId)
│   └── data/
│       └── KidsCampData.kt      # Source de vérité unique, jeu de données et persistance JSON
└── res/
    ├── layout/                  # Vues XML (activity_main, activity_children, etc.)
    └── drawable/                # Cartes arrondies, styles de boutons et ressources graphiques
