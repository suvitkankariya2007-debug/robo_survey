# Robo Survey

**An Android interface for robot telemetry, monitoring, and control.**

Robo Survey uses Kotlin, Jetpack Compose, and Firebase Realtime Database to bring robot status, sensor readings, waypoints, images, and commands into a single mobile interface.

[App navigation](app/src/main/java/com/example/robo_survey/MainActivity.kt) · [ViewModel](app/src/main/java/com/example/robo_survey/ui/RobotViewModel.kt) · [Data models](app/src/main/java/com/example/robo_survey/data/RobotData.kt) · [Screens](app/src/main/java/com/example/robo_survey/ui/screens)

## What is implemented

| Feature | Source behavior |
| --- | --- |
| Dashboard | Displays robot status, battery, alerts, connectivity, and sensor values |
| Live feed | Reads a camera-stream URL from Firebase configuration |
| Control | Writes operator commands and operating-mode changes |
| Map | Displays robot waypoint data |
| Gallery | Displays captured-image records |
| Supporting screens | Alert and event-log screen implementations are present in source |
| Reactive state | Firebase listeners update StateFlow values exposed by `RobotViewModel` |

The main bottom navigation currently exposes **five** screens: Dashboard, Live, Control, Map, and Gallery. Alert and event-log screen source files are additional implementations, not extra bottom-navigation destinations.

**Status:** Android project implementation. A functioning robot, telemetry producer, camera stream, and configured Firebase backend are external requirements; their availability is not implied by this repository.

## Data flow

1. A robot-side producer writes status, telemetry, waypoints, images, and logs into Firebase.
2. `RobotViewModel` subscribes with `ValueEventListener` callbacks.
3. StateFlow values drive the Compose interface.
4. Operator commands are written back to Firebase for a robot-side consumer to handle.

The robot-side producer/consumer is not included here.

## Firebase contract

| Database path | Contents or action |
| --- | --- |
| `robot/status` | `status`, `batteryLevel`, `isIntrusionDetected`, `alertLevel`, `isConnected`, `mode` |
| `robot/sensors` | `ultrasonicDistance`, `temperature`, `lightLevel`, `motionDetected`, `timestamp` |
| `robot/path` | Child records with `x`, `y`, `heading`, `timestamp` |
| `robot/captures` | Child records with `imageUrl`, `timestamp`, `label` |
| `robot/logs` | Child records with `id`, `type`, `message`, `timestamp` |
| `robot/arm` | `baseAngle`, `shoulderAngle`, `elbowAngle`, `gripperState` |
| `robot/config/camUrl` | Camera-stream URL string |
| `robot/command` | Operator command string written by the app |
| `robot/status/mode` | Operating-mode string written by the app |

Use the exact field names in [RobotData.kt](app/src/main/java/com/example/robo_survey/data/RobotData.kt); an empty Firebase database will not produce a realistic monitoring demonstration.

## Open and run

```bash
git clone https://github.com/suvitkankariya2007-debug/robo_survey.git
cd robo_survey
```

1. Open the repository in Android Studio.
2. Install Android SDK 35 and use the JDK required by the configured Android Gradle Plugin (8.7.0; JDK 17).
3. Register an Android app with package name `com.example.robo_survey` in **your own Firebase project** and configure Realtime Database.
4. Replace `app/google-services.json` with your project's Android configuration. Do not rely on the original project's backend.
5. Sync Gradle and run on an emulator or device with **Android API 26 or later**.
6. Supply compatible data at the paths above. Use a test database and test robot when exercising commands.

The module compiles against and targets SDK 35. Its Java/Kotlin source compatibility is set to 11; this is separate from the JDK used to run Gradle.

From a terminal with the Android SDK and JDK configured:

```bash
# macOS / Linux
sh ./gradlew assembleDebug

# Windows PowerShell
.\gradlew.bat assembleDebug
```

The expected debug APK path is `app/build/outputs/apk/debug/app-debug.apk`. An Android build was not executed during this documentation update.

## Code guide

| File or directory | Responsibility |
| --- | --- |
| [MainActivity.kt](app/src/main/java/com/example/robo_survey/MainActivity.kt) | App entry point and Compose navigation |
| [RobotViewModel.kt](app/src/main/java/com/example/robo_survey/ui/RobotViewModel.kt) | Database listeners, state streams, command writes |
| [RobotData.kt](app/src/main/java/com/example/robo_survey/data/RobotData.kt) | Telemetry, logs, images, waypoints, and arm-state models |
| [ui/screens/](app/src/main/java/com/example/robo_survey/ui/screens) | Monitoring and control presentation |
| [app/build.gradle.kts](app/build.gradle.kts) | Android SDK settings and dependencies |

## Engineering focus

- Keeping presentation, data models, and state management separate.
- Coordinating several real-time data streams in one interface.
- Sorting logs and waypoints by timestamp before presentation.
- Using one ViewModel to share robot state across navigation destinations.

## Current boundaries

Firebase authentication/rules, listener cleanup, comprehensive error/offline handling, the robot command protocol, and hardware integration need further work. The manifest currently allows cleartext traffic for development camera feeds; transport configuration needs review before deployment. Firebase Messaging is a dependency, not evidence of a complete notification delivery implementation.

The connection indicator is based on status-listener callbacks; it is not a dedicated robot heartbeat or a guarantee that the physical device is online. Existing test files are starter examples rather than full ViewModel or device-integration coverage.
