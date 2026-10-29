# NUTRIX — Privacy and Security

## 1. Purpose

NUTRIX is an Android nutrition companion designed to help users explore food information, manage food records, and organize personal nutrition goals.

This document outlines privacy and security principles for the project. It should be updated to reflect the application's actual data handling and integrations as they evolve.

## 2. Data Categories

Depending on enabled features, the application may process:

- Food selections and nutrition information.
- Food history and serving quantities.
- Dietary goals and preferences.
- Hydration records and reminder settings.
- User-submitted questions to an AI assistant.

The application should collect only the information needed to provide its features.

## 3. Local Storage

Where supported, user records should be stored locally on the device to support convenient access and reduce unnecessary data transmission.

Developers should document the actual storage implementation, the information retained, and whether users can clear their records.

Local storage should not be described as fully secure or exclusively offline without verifying the relevant implementation.

## 4. Network Requests and External Services

Some functionality, such as retrieving packaged-food information or using an external AI service, may require network access.

Before sending data to an external service, the application should:

- Limit requests to the information necessary for the feature.
- Avoid transmitting unrelated personal records.
- Handle network failures and API errors safely.
- Explain relevant external data processing to users.
- Follow the applicable service provider's terms and privacy requirements.

The services actually used should be listed here when confirmed in the source code.

## 5. API Keys and Credentials

API keys, access tokens, passwords, and signing credentials must not be committed to a public GitHub repository.

Recommended practices:

- Keep private credentials out of source files and Git history.
- Use an appropriate backend or secure configuration strategy for production secrets.
- Avoid embedding privileged API keys in a publicly distributed Android application.
- Rotate credentials immediately if they are exposed.
- Keep local machine configuration out of version control.

An API key embedded in an APK may be extracted. Obfuscation alone does not make a secret safe.

## 6. Android Permissions

NUTRIX should request only the permissions required by its enabled features.

Potential permissions may include:

- Camera access for supported food-scanning workflows.
- Notification permission for reminders on Android versions that require it.
- Network access for online services.

The exact permission list must match `app/src/main/AndroidManifest.xml` and the application's actual behavior.

Users should be informed why a permission is needed, and the app should handle denied permissions gracefully.

## 7. Food and Nutrition Information

Food records and nutrition preferences may be personal information.

The application should avoid unnecessary disclosure of these records and should clearly explain whether any information is sent to external services.

Nutrition ratings and estimates should be presented transparently, with their limitations made clear.

## 8. Data Retention and Deletion

The project's privacy documentation should specify:

- What information is saved.
- Where it is stored.
- How long it is retained.
- How users can delete saved information.
- Whether external providers retain submitted data.

These details should be finalized according to the actual implementation.

## 9. Public Repository Safety

Before publishing changes:

1. Review staged files with `git status`.
2. Check that `.env` files, private keys, local configuration, and credentials are excluded.
3. Avoid committing personal logs or private datasets.
4. Review dependency and configuration changes before pushing.
5. Remove exposed credentials from the relevant service if a leak occurs; deleting a file from the latest commit alone does not erase it from Git history.

## 10. Responsible AI Usage

AI-generated nutrition information may be incomplete or inaccurate.

The application should not present generated advice as a medical diagnosis or substitute for qualified professional guidance. Users with medical conditions or specialized dietary needs should consult an appropriate healthcare professional.

## 11. Scope and Accuracy

This document describes security principles and intended safeguards. It is not a claim that every control has been implemented or independently audited.

The documentation should be revised whenever the application adds external services, changes storage behavior, or introduces new permissions.

---

**Project:** NUTRIX  
**Platform:** Android  
**Repository:** https://github.com/Dinesh31698/NUTRIX
