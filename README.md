# OTP Detector

An Android LSPosed module that automatically detects One-Time Passwords (OTPs) from notifications (specifically Google Voice) and copies them to the clipboard.

## Features
- **Automatic Detection**: Listens for OTP patterns in incoming notifications.
- **Clipboard Integration**: Automatically copies detected codes to the clipboard.
- **Lightweight**: Minimal battery and resource impact.

## Prerequisites
- **Java JDK 21+**
- **Android SDK**
- **Mise** (optional, for task management)

## Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Jkker/otp-detector.git
   cd otp-detector
   ```

2. **Open in Android Studio**:
   - Open the `otp-detector` directory.
   - Sync Gradle.

## Development Tasks (using Mise)

This project uses [mise](https://mise.jdx.dev/) for managing tools and tasks.

- **Build**: Assemble the debug APK.
  ```bash
  mise run build
  ```

- **Test**: Run unit tests.
  ```bash
  mise run test
  ```

- **Lint**: Run lint checks.
  ```bash
  mise run lint
  ```

- **Clean**: Clean the project.
  ```bash
  mise run clean
  ```

## Publishing
This project is configured to be release-ready. Ensure you have your `release.keystore` configured in `local.properties` or environment variables for signed builds (currently configured in `build.gradle.kts` to look for `release.keystore` file).
