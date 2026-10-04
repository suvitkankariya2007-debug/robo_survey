# Robo Survey

Android application for monitoring and controlling a surveillance robot through a Firebase-backed data layer.

## Overview

Robo Survey was built as a hackathon-style robotics interface that connects a mobile application to robot telemetry and control data. The app focuses on giving an operator a single place to view robot status, sensor readings, captured images, path information, alerts, and control commands.

The current implementation is written in Kotlin with Jetpack Compose and uses Firebase Realtime Database as the shared communication layer.

## Core features

- **Dashboard** for robot status, battery, alert level, connection state, and sensor information
- **Live-feed screen** driven by a remotely configured camera-stream URL
- **Control screen** for sending robot commands and changing operating mode
- **Map/path view** for displaying timestamped robot waypoints
- **Gallery** for captured surveillance images
- **Event logging** for system, intrusion, and battery events
- **Robot-arm state** for base, shoulder, elbow, and gripper values
- **Firebase-backed state synchronization** using database listeners
- **StateFlow-based UI state** through an Android ViewModel

## Data model

The application tracks several types of robot data:

### Robot status
- status
- battery level
- intrusion flag
- alert level
- connection state
- operating mode

### Sensor readings
- ultrasonic distance
- temperature
- light level
- motion-detection state
- timestamp

### Path data
- x/y coordinates
- heading
- timestamp

### Captures
- image URL
- timestamp
- label such as Patrol or Intrusion

### Arm state
- base angle
- shoulder angle
- elbow angle
- gripper state

## Architecture

```text
Android UI (Jetpack Compose)
        │
        ▼
RobotViewModel
        │
        ├── StateFlow<RobotStatus>
        ├── StateFlow<SensorReading>
        ├── StateFlow<List<PathWaypoint>>
        ├── StateFlow<List<CapturedImage>>
        ├── StateFlow<List<EventLog>>
        └── StateFlow<ArmState>
        │
        ▼
Firebase Realtime Database
        │
        ├── robot/status
        ├── robot/sensors
        ├── robot/path
        ├── robot/captures
        ├── robot/logs
        ├── robot/command
        ├── robot/config/camUrl
        └── robot/arm
```

## App navigation

The current main navigation includes:

1. Dashboard
2. Live Feed
3. Control
4. Map
5. Gallery

Additional alert and event-log screens are also implemented in the project source.

## Tech stack

- Kotlin
- Jetpack Compose
- Android ViewModel
- Kotlin StateFlow
- Firebase Realtime Database
- Firebase Cloud Messaging dependencies
- Navigation Compose
- Coil for image loading
- Material 3

## How commands flow

The app writes operator commands to:

```text
robot/command
```

and operating-mode changes to:

```text
robot/status/mode
```

Telemetry is received through Firebase `ValueEventListener` callbacks and pushed into StateFlow objects consumed by the Compose UI.

## Project structure

```text
app/src/main/java/com/example/robo_survey/
├── MainActivity.kt
├── data/
│   └── RobotData.kt
└── ui/
    ├── RobotViewModel.kt
    ├── screens/
    │   ├── DashboardScreen.kt
    │   ├── LiveFeedScreen.kt
    │   ├── ControlScreen.kt
    │   ├── MapScreen.kt
    │   ├── GalleryScreen.kt
    │   ├── AlertScreen.kt
    │   └── EventLogsScreen.kt
    └── theme/
```

## Running the project

1. Open the project in Android Studio.
2. Add/configure the Firebase project used by the application.
3. Sync Gradle dependencies.
4. Run the app on Android API 26 or above.

The current project includes Google Services configuration files, but for a reusable public deployment those credentials/configuration should be replaced with your own Firebase setup.

## What I learned

- structuring an Android app with ViewModel + reactive state
- listening to and transforming real-time backend data
- coordinating multiple telemetry streams in one interface
- building navigation and state-driven screens with Jetpack Compose
- separating data models, state management, and presentation

## Current limitations / next improvements

- add a documented robot-side communication protocol
- improve authentication/security rules for Firebase
- add offline/error-state handling
- add automated UI and ViewModel tests
- document the camera-stream format and hardware integration
- move environment-specific Firebase configuration out of source control where appropriate

## Repository

**GitHub:** https://github.com/suvitkankariya2007-debug/robo_survey
