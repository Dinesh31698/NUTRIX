# NUTRIX — Project Structure

## Overview

NUTRIX is a native Android application developed using Kotlin, Jetpack Compose, and Gradle. Its source code follows a feature-oriented organization with shared components for application data, nutrition calculations, UI design, networking, and notifications.

This document explains the repository structure and the responsibilities of its major components.

## Repository Layout

```text
NUTRIX/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nutrix/app/
│   │   │   │   ├── core/
│   │   │   │   │   ├── database/
│   │   │   │   │   ├── designsystem/
│   │   │   │   │   ├── engine/
│   │   │   │   │   ├── model/
│   │   │   │   │   ├── network/
│   │   │   │   │   └── notifications/
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── seed/
│   │   │   │   ├── feature/
│   │   │   │   │   ├── assistant/
│   │   │   │   │   ├── dietplan/
│   │   │   │   │   ├── fooddetails/
│   │   │   │   │   ├── foodhistory/
│   │   │   │   │   ├── foodsearch/
│   │   │   │   │   ├── grocery/
│   │   │   │   │   ├── home/
│   │   │   │   │   ├── onboarding/
│   │   │   │   │   ├── profile/
│   │   │   │   │   ├── progress/
│   │   │   │   │   ├── reminders/
│   │   │   │   │   ├── scanner/
│   │   │   │   │   ├── scanresult/
│   │   │   │   │   └── welcome/
│   │   │   │   └── navigation/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
│
├── docs/
│   ├── ARCHITECTURE.md
│   ├── PRIVACY_AND_SECURITY.md
│   └── PROJECT_STRUCTURE.md
│
├── screenshots/
│   ├── AI.png
│   ├── DitePlan.png
│   ├── FoodHistory.png
│   ├── FoodQuantity.png
│   ├── HomeScreen.png
│   ├── SearchFood.png
│   └── Settings.png
│
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│
├── .gitignore
├── CONTRIBUTING.md
├── README.md
├── ROADMAP.md
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

## Main Directories

### `app/`

Contains the Android application module, including source code, resources, module configuration, and unit tests.

### `core/`

Contains shared application components.

- `database/` — database entities, data access objects, and database configuration.
- `designsystem/` — shared UI components, themes, colors, and visual helpers.
- `engine/` — nutrition calculations, health ratings, diet suggestions, and assistant logic.
- `model/` — food and nutrition data models.
- `network/` — external food-data integration.
- `notifications/` — reminder scheduling and notification handling.

### `data/`

Contains data access and repository-related components.

- `repository/` — coordinates application data operations.
- `seed/` — initial food data.

### `feature/`

Contains feature-specific screens and user workflows.

- `assistant/` — AI assistant.
- `dietplan/` — diet planning.
- `fooddetails/` — food details.
- `foodhistory/` — food history.
- `foodsearch/` — food search.
- `grocery/` — grocery management.
- `home/` — home dashboard.
- `onboarding/` — onboarding workflow.
- `profile/` — profile settings.
- `progress/` — progress tracking.
- `reminders/` — reminder settings.
- `scanner/` — food scanning.
- `scanresult/` — scan result presentation.
- `welcome/` — welcome screen.

### `navigation/`

Contains application navigation components.

### `res/`

Contains Android resources, including launcher icons, themes, and localized string resources.

### `test/`

Contains unit tests for application logic, including nutrition calculations, health ratings, diet suggestions, and assistant functionality.

### `docs/`

Contains project architecture, structure, and privacy documentation.

### `screenshots/`

Contains visual references of the NUTRIX Android application for the GitHub project page.

## Root Configuration Files

| File | Purpose |
|---|---|
| `build.gradle.kts` | Root Gradle configuration |
| `app/build.gradle.kts` | Android application module configuration |
| `settings.gradle.kts` | Project and module configuration |
| `gradle.properties` | Gradle properties |
| `gradle/libs.versions.toml` | Dependency and plugin version catalog |
| `gradlew` | Gradle wrapper for Unix-like systems |
| `gradlew.bat` | Gradle wrapper for Windows |
| `.gitignore` | Excludes selected files from version control |
| `README.md` | Main project documentation |
| `CONTRIBUTING.md` | Contribution guidelines |
| `ROADMAP.md` | Future development direction |

## Development Workflow

1. Open the repository in Android Studio.
2. Allow Gradle synchronization to complete.
3. Make changes within the relevant source packages.
4. Keep documentation synchronized with significant changes.
5. Review modifications before committing.
6. Commit and push intended changes to GitHub.

## Version Control and Security

Do not commit API keys, passwords, signing credentials, or private configuration.

Generated build files, IDE-specific settings, and local machine configuration should remain outside version control unless explicitly required.

## Documentation Accuracy

This document describes the repository organization and intended component responsibilities. Feature behavior and external integrations should be documented according to the actual implementation.

---

**Project:** NUTRIX  
**Platform:** Android  
**Language:** Kotlin  
**Build System:** Gradle with Kotlin DSL  
**Repository:** https://github.com/Dinesh31698/NUTRIX
