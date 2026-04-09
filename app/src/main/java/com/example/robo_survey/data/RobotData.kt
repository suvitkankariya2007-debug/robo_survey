package com.example.robo_survey.data

data class RobotStatus(
    val status: String = "Idle",
    val batteryLevel: Int = 0,
    val isIntrusionDetected: Boolean = false,
    val isConnected: Boolean = false,
    val mode: String = "Manual"
)

data class EventLog(
    val id: String = "",
    val type: String = "Info", // Intrusion, Battery, System
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
