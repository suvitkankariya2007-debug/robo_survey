package com.example.robo_survey.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robo_survey.data.EventLog
import com.example.robo_survey.data.RobotStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RobotViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val statusRef = database.getReference("robot/status")
    private val logsRef = database.getReference("robot/logs")
    private val commandRef = database.getReference("robot/command")

    private val _robotStatus = MutableStateFlow(RobotStatus())
    val robotStatus: StateFlow<RobotStatus> = _robotStatus

    private val _eventLogs = MutableStateFlow<List<EventLog>>(emptyList())
    val eventLogs: StateFlow<List<EventLog>> = _eventLogs

    init {
        listenToStatus()
        listenToLogs()
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

    fun sendCommand(command: String) {
        commandRef.setValue(command)
    }

    fun setMode(mode: String) {
        statusRef.child("mode").setValue(mode)
    }
}
