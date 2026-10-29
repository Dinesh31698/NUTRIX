# NUTRIX — Project Structure

## Overview

NUTRIX is an Android application developed using Kotlin and Gradle. This document describes the repository's main directories and configuration files to help developers navigate the codebase.

## Repository Layout

```text
NUTRIX/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nutrix/app/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── docs/
│   ├── ARCHITECTURE.md
│   └── PROJECT_STRUCTURE.md
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

## Main Directories

### `app/`

Contains the Android application module, including Kotlin source code, resources, application configuration, and tests.

### `app/src/main/java/com/nutrix/app/`

Contains the application's Kotlin packages, organized around shared components and feature areas.

- `core/` — shared application components, data models, database functionality, design system, engines, networking, and notifications.
- `data/` — repository and food-data components.
- `feature/` — feature-specific screens such as home, scanning, food search, food details, history, diet planning, groceries, reminders, profile, progress, and AI assistance.
- `navigation/` — application navigation components.

These descriptions reflect the package structure present in the initial project source commit. Individual responsibilities may evolve as the application develops.

### `app/src/main/res/`

Contains Android resources, including launcher icons, themes, and localized string resources.

### `app/src/test/`

Contains unit tests for application logic, including nutrition calculations, health ratings, diet suggestions, and AI assistant functionality.

### `gradle/`

Contains Gradle wrapper support files and the version catalog used to organize dependency versions.

## Root Configuration Files

| File | Purpose |
|---|---|
| `build.gradle.kts` | Root Gradle build configuration |
| `app/build.gradle.kts` | Android application module configuration |
| `settings.gradle.kts` | Project and module settings |
| `gradle.properties` | Gradle build properties |
| `gradle/libs.versions.toml` | Centralized dependency and plugin versions |
| `gradlew` | Gradle wrapper script for Unix-like systems |
| `gradlew.bat` | Gradle wrapper script for Windows |
| `proguard-rules.pro` | Rules for code shrinking and obfuscation |
| `.gitignore` | Excludes selected files from version control |
| `README.md` | Project introduction and setup instructions |

## Documentation

- `README.md` — project overview, features, and setup.
- `docs/ARCHITECTURE.md` — conceptual application architecture and data flow.
- `docs/PROJECT_STRUCTURE.md` — repository organization and file responsibilities.

## Development Workflow

1. Open the project root in Android Studio.
2. Allow Gradle to synchronize the project.
3. Make changes within the relevant source packages.
4. Keep documentation synchronized with meaningful code changes.
5. Review Git changes before committing.
6. Push commits to the GitHub repository.

## Version Control and Security

Do not commit API keys, passwords, signing credentials, or other secrets. Keep local machine configuration and generated build artifacts out of version control.

## Documentation Accuracy

This document describes the repository structure and intended responsibilities of its components. Feature behavior and external integrations should be documented separately and should reflect the actual implementation.

---

**Project:** NUTRIX  
**Platform:** Android  
**Language:** Kotlin  
**Build system:** Gradle with Kotlin DSL  
**Repository:** https://github.com/Dinesh31698/NUTRIX
