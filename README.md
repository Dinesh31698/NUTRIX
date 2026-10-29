
# NUTRIX — AI-Powered Food Intelligence & Personal Nutrition

NUTRIX is an Android nutrition companion designed to help users make more informed food choices, understand nutritional information, track eating habits, and build healthier daily routines through food analysis, personalized nutrition goals, and intelligent assistance.

The project aims to bring food awareness, nutrition tracking, and practical dietary guidance together in one intuitive mobile application.

## Overview

Understanding what we eat is an important part of maintaining a balanced lifestyle. NUTRIX is designed to make that process easier by combining food scanning, nutrition information, health ratings, food history, diet planning, hydration tracking, and personalized reminders in one place.

Whether the goal is healthy weight gain, weight loss, muscle gain, weight maintenance, or balanced nutrition, NUTRIX aims to help users make more informed everyday decisions.

## Features

### Food Scanning & Recognition
- Scan supported packaged food products.
- Identify supported individual food items using the available recognition functionality.
- Search for Indian foods and dishes by name.
- View food-related nutritional information.
- Enter serving quantities in grams or millilitres where supported.
- Explore healthier alternatives and food insights where available.

### Nutrition Analysis
- View estimated nutritional information, including calories and macronutrients where available.
- Receive a food health rating on a 1–5 scale.
- Understand food choices through accessible nutrition summaries.
- Adjust serving quantities to make nutritional estimates more relevant.
- Use food information to support better everyday eating decisions.

*Nutrition values may be estimates and can vary by ingredients, preparation methods, brand, and serving size.*

### Food History & Tracking
- Record foods consumed throughout the day.
- Review previously logged meals and food items.
- Track calorie intake and available nutritional summaries.
- Review recent eating patterns to support more consistent habits.

### Personalized Diet Planning
- Choose a nutrition goal:
  - Healthy weight gain
  - Weight loss
  - Muscle gain
  - Weight maintenance
  - Balanced nutrition
- Organize meals around personal goals.
- Review current and upcoming weekly diet plans where supported.
- Adjust plans as dietary needs and preferences change.

### Hydration Tracking
- Track daily water consumption.
- Work toward a daily hydration target.
- Review recorded water intake and progress.

### Smart Reminders
- Configure reminders for meals, water intake, and daily routines where supported.
- Customize reminder preferences and schedules.
- Receive timely prompts to maintain consistent habits.

### AI Nutrition Assistant
- Ask food and nutrition-related questions.
- Get help understanding food information.
- Explore meal ideas and healthier food choices.
- Receive practical guidance aligned with personal nutrition goals, subject to the assistant's implemented capabilities.

### Personalization & Accessibility
- Support for a premium dark-themed interface.
- Designed around modern visual elements and clear information presentation.
- Intended support for English, Telugu, and Hindi, depending on the implemented language resources.
- Serving-size and preference customization where available.

### Local Data & Privacy
- Designed with an emphasis on keeping personal food logs and preferences under the user's control.
- Local storage can support access to saved records without requiring a continuous internet connection.
- Network-dependent features may require internet access.
- Actual offline availability depends on the implementation of each feature.

## Technology Stack

| Component | Technology |
|---|---|
| Platform | Android |
| Programming Language | Kotlin |
| IDE | Android Studio |
| Build System | Gradle |
| Build Configuration | Kotlin DSL |
| UI Framework | Android UI components used by the project |
| Minimum Android Version | See `app/build.gradle.kts` |
| Target Android Version | See `app/build.gradle.kts` |

Additional libraries and services depend on the modules and dependencies configured in the project.

## Application Architecture

NUTRIX is organized as an Android application using a Gradle-based project structure.

```text
NUTRIX/
├── app/
│   └── src/
│       └── main/
│           ├── java/ or kotlin/
│           ├── res/
│           └── AndroidManifest.xml
├── gradle/
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

The `app` module contains the Android application source code and resources. The root Gradle files configure the project and its build process.

The architecture may evolve as food recognition, nutrition calculations, local persistence, notifications, and AI integration are developed further.

## Getting Started

### Prerequisites

- A computer running Windows, macOS, or Linux.
- Android Studio.
- A compatible JDK supported by the project's Gradle configuration.
- Android SDK components required by the project.
- An Android emulator or a compatible Android device.

### Clone the Repository

```bash
git clone https://github.com/Dinesh31698/NUTRIX.git
cd NUTRIX
```

### Open in Android Studio

1. Launch Android Studio.
2. Select **Open**.
3. Choose the cloned `NUTRIX` directory.
4. Allow Gradle synchronization to finish.
5. Install any missing SDK components requested by Android Studio.

### Run the Application

1. Start an Android emulator or connect an Android device with USB debugging enabled.
2. Select the application run configuration.
3. Click **Run** in Android Studio.
4. Wait for the app to install and launch.

### Build from the Command Line

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

To run the available unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

The debug APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The exact output location can vary with the project's Gradle configuration.

## Configuration & Security

- Do not commit API keys, access tokens, passwords, signing credentials, or other secrets.
- Keep machine-specific configuration such as `local.properties` out of version control.
- Configure any external AI or nutrition service credentials locally or through an appropriate secure configuration mechanism.
- Review third-party SDK permissions and privacy policies before enabling external integrations.

## Responsible Nutrition

NUTRIX is intended to support food awareness and general nutrition education. Its ratings, estimates, and recommendations should not be treated as medical diagnoses or substitutes for professional dietary advice.

Nutritional requirements vary between individuals. Consult a qualified healthcare professional for advice related to medical conditions, allergies, or specialized dietary needs.

## Roadmap

Potential areas for future development include:

- Improved packaged-food scanning and product recognition.
- More comprehensive Indian food and regional dish coverage.
- Better serving-size and nutrition estimation.
- Personalized meal planning and weekly progress summaries.
- More detailed food history and nutritional analytics.
- Enhanced AI-powered food assistance.
- Improved offline functionality and local data management.
- Expanded multilingual support.
- Accessibility improvements and additional UI refinements.

Roadmap items are planned possibilities and may not yet be implemented.

## Contributing

Suggestions, bug reports, and improvements are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make and test your changes.
4. Submit a pull request describing the improvement.

For bug reports, include the Android version, steps to reproduce the issue, and relevant error messages. Never include private credentials or personal data.

## License

No license has been specified yet. Unless a license is added to this repository, the default copyright rules apply. Check with the project owner before copying, modifying, or redistributing the source code.

## Repository

**GitHub:** https://github.com/Dinesh31698/NUTRIX

---

Built with Kotlin and Android Studio, with the goal of making everyday nutrition information easier to understand and use.
