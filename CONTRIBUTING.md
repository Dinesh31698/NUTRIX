# Contributing to NUTRIX

Thank you for your interest in NUTRIX!

NUTRIX is an Android application focused on food intelligence, nutrition awareness, food tracking, and personalized dietary management. Contributions, suggestions, documentation improvements, and bug reports are welcome.

## Ways to Contribute

You can contribute by:

- Improving the Android user interface and accessibility.
- Enhancing food search and packaged-food identification.
- Improving nutrition calculations and food data quality.
- Developing food history and dietary planning features.
- Improving hydration tracking and reminders.
- Enhancing AI-assisted nutrition guidance.
- Improving localization and language support.
- Fixing bugs and improving maintainability.
- Improving documentation.

## Before You Begin

1. Review the project's `README.md`.
2. Read the documentation in the `docs/` directory.
3. Check existing issues and pull requests to avoid duplicate work.
4. For significant changes, discuss the proposed approach with the project owner before starting.

## Development Setup

### Requirements

- Android Studio.
- A compatible JDK and Android SDK.
- Git.
- A compatible Android device or emulator if you intend to run the application.

### Clone the Repository

```bash
git clone https://github.com/Dinesh31698/NUTRIX.git
cd NUTRIX
```

Open the project directory in Android Studio and allow Gradle synchronization to complete.

## Contribution Workflow

### 1. Create a Branch

Use a descriptive branch name.

```bash
git checkout -b feature/your-feature-name
```

For bug fixes, a name such as `fix/your-fix-name` is appropriate.

### 2. Make Your Changes

Keep changes focused and consistent with the existing Kotlin and Android project conventions.

Avoid unrelated modifications in the same pull request.

### 3. Review Your Changes

Before committing:

- Review the modified files.
- Remove temporary files and debugging output.
- Check that credentials and private configuration are not included.
- Update documentation when behavior or project structure changes.
- Run relevant checks when possible.

### 4. Commit Your Changes

Use a clear commit message.

```bash
git add .
git commit -m "Describe your change"
```

Review `git status` before staging files to ensure that only intended changes are included.

### 5. Push Your Branch

```bash
git push -u origin feature/your-feature-name
```

Replace the example branch name with your actual branch name.

### 6. Open a Pull Request

Submit a pull request against the repository's `main` branch.

Include:

- A clear summary of the change.
- The reason for the change.
- Relevant screenshots for UI changes, if available.
- Any configuration or documentation changes.
- Known limitations.

## Coding Guidelines

- Follow existing Kotlin formatting and naming conventions.
- Prefer readable, maintainable code over unnecessary complexity.
- Keep UI, application logic, and data responsibilities organized.
- Handle errors and unavailable data gracefully.
- Avoid hardcoding secrets, credentials, or environment-specific paths.
- Keep nutrition calculations and rating criteria understandable.
- Avoid presenting estimated nutrition values as verified facts.

## Nutrition and AI Contributions

Changes involving food ratings, nutrition calculations, or AI-generated guidance should clearly explain their assumptions and limitations.

Do not introduce unsupported medical claims or represent general nutrition guidance as a diagnosis or treatment plan.

## Reporting Issues

When reporting a bug, provide:

- A clear description of the problem.
- Steps to reproduce it, if known.
- Expected and actual behavior.
- Relevant Android or build configuration details.
- Screenshots or error logs when useful.

Remove personal information, access tokens, and other secrets before sharing logs.

## Security Issues

Do not publish API keys, passwords, private signing keys, or sensitive user data in issues or pull requests.

If you discover a security issue, contact the project owner privately rather than publicly exposing exploitable details.

## License and Contributions

No license has been specified in this repository's documentation yet. Please contact the project owner before assuming that the code may be redistributed or reused under an open-source license.

## Thank You

Thank you for helping improve NUTRIX and making food and nutrition information easier to understand and use.

**Repository:** https://github.com/Dinesh31698/NUTRIX
