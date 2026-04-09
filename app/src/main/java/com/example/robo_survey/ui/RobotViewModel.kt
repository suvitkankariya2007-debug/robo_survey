package com.example.robo_survey.ui

import androidx.lifecycle.ViewModel
import com.example.robo_survey.data.CapturedImage
import com.example.robo_survey.data.EventLog
import com.example.robo_survey.data.PathWaypoint
import com.example.robo_survey.data.RobotStatus
import com.example.robo_survey.data.SensorReading
import com.example.robo_survey.data.ArmState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RobotViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val statusRef = database.getReference("robot/status")
    private val logsRef = database.getReference("robot/logs")
    private val commandRef = database.getReference("robot/command")
    private val sensorsRef = database.getReference("robot/sensors")
    private val pathRef = database.getReference("robot/path")
    private val capturesRef = database.getReference("robot/captures")
    private val camUrlRef = database.getReference("robot/config/camUrl")
    private val armRef = database.getReference("robot/arm")

    private val _robotStatus = MutableStateFlow(RobotStatus())
    val robotStatus: StateFlow<RobotStatus> = _robotStatus

    private val _eventLogs = MutableStateFlow<List<EventLog>>(emptyList())
    val eventLogs: StateFlow<List<EventLog>> = _eventLogs

    private val _sensorData = MutableStateFlow(SensorReading())
    val sensorData: StateFlow<SensorReading> = _sensorData

    private val _pathWaypoints = MutableStateFlow<List<PathWaypoint>>(emptyList())
    val pathWaypoints: StateFlow<List<PathWaypoint>> = _pathWaypoints

    private val _capturedImages = MutableStateFlow<List<CapturedImage>>(emptyList())
    val capturedImages: StateFlow<List<CapturedImage>> = _capturedImages

    private val _camStreamUrl = MutableStateFlow("")
    val camStreamUrl: StateFlow<String> = _camStreamUrl

    private val _armState = MutableStateFlow(ArmState())
    val armState: StateFlow<ArmState> = _armState

    init {
        listenToStatus()
        listenToLogs()
        listenToSensors()
        listenToPath()
        listenToCaptures()
        listenToCamUrl()
        listenToArm()
    }

    private fun listenToStatus() {
        statusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(RobotStatus::class.java)
                status?.let {
                    _robotStatus.value = it.copy(isConnected = true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _robotStatus.value = _robotStatus.value.copy(isConnected = false)
            }
        })
    }

    private fun listenToLogs() {
        logsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val logs = mutableListOf<EventLog>()
                snapshot.children.forEach {
                    it.getValue(EventLog::class.java)?.let { log -> logs.add(log) }
                }
                _eventLogs.value = logs.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToSensors() {
        sensorsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reading = snapshot.getValue(SensorReading::class.java)
                reading?.let { _sensorData.value = it }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToPath() {
        pathRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val waypoints = mutableListOf<PathWaypoint>()
                snapshot.children.forEach {
                    it.getValue(PathWaypoint::class.java)?.let { wp -> waypoints.add(wp) }
                }
                _pathWaypoints.value = waypoints.sortedBy { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToCaptures() {
        capturesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = mutableListOf<CapturedImage>()
                snapshot.children.forEach {
                    it.getValue(CapturedImage::class.java)?.let { img -> images.add(img) }
                }
                _capturedImages.value = images.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToCamUrl() {
        camUrlRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val url = snapshot.getValue(String::class.java)
                _camStreamUrl.value = url ?: ""
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToArm() {
        armRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(ArmState::class.java)
                state?.let { _armState.value = it }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendCommand(command: String) {
        commandRef.setValue(command)
    }

    fun setMode(mode: String) {
        statusRef.child("mode").setValue(mode)
    }

    fun clearPath() {
        pathRef.removeValue()
    }
}
