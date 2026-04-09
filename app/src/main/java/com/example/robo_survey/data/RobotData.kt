package com.example.robo_survey.data

data class RobotStatus(
    val status: String = "Idle",
    val batteryLevel: Int = 0,
    val isIntrusionDetected: Boolean = false,
    val alertLevel: String = "Normal", // "Normal", "Suspicious", "Intrusion"
    val isConnected: Boolean = false,
    val mode: String = "Manual"
)

data class EventLog(
    val id: String = "",
    val type: String = "Info", // Intrusion, Battery, System
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SensorReading(
    val ultrasonicDistance: Float = 0f,
    val temperature: Float = 0f,
    val lightLevel: Float = 0f,
    val motionDetected: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class PathWaypoint(
    val x: Float = 0f,
    val y: Float = 0f,
    val heading: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

data class CapturedImage(
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val label: String = "Patrol" // Intrusion, Patrol
)

data class ArmState(
    val baseAngle: Int = 90,
    val shoulderAngle: Int = 90,
    val elbowAngle: Int = 90,
    val gripperState: Int = 0
)
