package com.example.robo_survey.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.robo_survey.data.EventLog
import com.example.robo_survey.ui.RobotViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: RobotViewModel) {
    val status by viewModel.robotStatus.collectAsState()
    val sensors by viewModel.sensorData.collectAsState()
    val logs by viewModel.eventLogs.collectAsState()
    val armState by viewModel.armState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Robot Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Connection Status Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (status.isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (status.isConnected) Color.Green else Color.Red)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (status.isConnected) "SYSTEM ONLINE" else "SYSTEM OFFLINE",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (status.isConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Battery & Mode Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Battery Gauge Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BatteryGauge(level = status.batteryLevel)
                    }
                }

                // State Card
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusCardSmall(
                        title = "State",
                        value = status.status,
                        icon = Icons.Default.PrecisionManufacturing,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatusCardSmall(
                        title = "Mode",
                        value = status.mode,
                        icon = Icons.Default.Tune,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }

        // Security Alert Banner
        item {
            val alertTitle = when(status.alertLevel) {
                "Intrusion" -> "INTRUSION DETECTED"
                "Suspicious" -> "SUSPICIOUS ACTIVITY"
                else -> "SYSTEM SECURE"
            }
            val alertDesc = when(status.alertLevel) {
                "Intrusion" -> "Immediate action required!"
                "Suspicious" -> "Motion detected! Review live feed."
                else -> "No threats detected."
            }
            val alertColor = when(status.alertLevel) {
                "Intrusion" -> Color(0xFFB71C1C) // Red
                "Suspicious" -> Color(0xFF1565C0) // Blue
                else -> Color(0xFF2E7D32) // Green
            }
            val alertIcon = when(status.alertLevel) {
                "Intrusion" -> Icons.Default.Warning
                "Suspicious" -> Icons.Default.Visibility
                else -> Icons.Default.Security
            }

            val infiniteTransition = rememberInfiniteTransition(label = "blink")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (status.alertLevel == "Intrusion") 0.4f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                ), label = "alpha"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = alertColor)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        alertIcon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = alpha),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(alertTitle, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text(alertDesc, color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }
        }

        // Sensor Readings Section
        item {
            Text(
                text = "Sensor Readings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SensorCard(
                    label = "Distance",
                    value = "%.1f cm".format(sensors.ultrasonicDistance),
                    icon = Icons.Default.Sensors,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )

                // Motion Detection Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    val motionColor = if (sensors.motionDetected) Color(0xFFE91E63) else Color.Gray

                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = motionColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = motionColor,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (sensors.motionDetected) "DETECTED" else "CLEAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = motionColor
                        )
                        Text(
                            text = "Motion",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Robotic Arm Tracker Section
        item {
            Text(
                text = "Robotic Arm Tracker",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServoTracker("Base Angle", armState.baseAngle, Color(0xFFE91E63))
                    ServoTracker("Shoulder Angle", armState.shoulderAngle, Color(0xFF9C27B0))
                    ServoTracker("Elbow Angle", armState.elbowAngle, Color(0xFF3F51B5))
                    ServoTracker("Gripper State", armState.gripperState, Color(0xFF00BCD4))
                }
            }
        }

        // Recent Logs Section
        item {
            Text(
                text = "Recent Logs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val recentLogs = logs.take(5)
        if (recentLogs.isEmpty()) {
            item {
                Text(
                    text = "No logs available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentLogs) { log ->
                RecentLogItem(log)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun BatteryGauge(level: Int) {
    val color = when {
        level > 60 -> Color(0xFF4CAF50)
        level > 20 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    val animatedLevel by animateFloatAsState(
        targetValue = level.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "battery"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(100.dp)) {
                // Background arc
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(12f, 12f),
                    size = Size(size.width - 24f, size.height - 24f)
                )
                // Filled arc
                drawArc(
                    color = color,
                    startAngle = 135f,
                    sweepAngle = 270f * (animatedLevel / 100f),
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(12f, 12f),
                    size = Size(size.width - 24f, size.height - 24f)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BatteryFull,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${level}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Battery", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun StatusCardSmall(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SensorCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecentLogItem(log: EventLog) {
    val icon = when (log.type) {
        "Intrusion" -> Icons.Default.Warning
        "Battery" -> Icons.Default.BatteryAlert
        else -> Icons.Default.Info
    }
    val color = when (log.type) {
        "Intrusion" -> Color(0xFFF44336)
        "Battery" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = log.message, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ServoTracker(label: String, angle: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = "${angle}°", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        
        // Progress bar for 0-180 range
        val progress by animateFloatAsState(
            targetValue = angle / 180f,
            animationSpec = tween(500),
            label = "ServoProgress"
        )
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )
    }
}
