package com.example.robo_survey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun AlertScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Robot Operation Modes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 1. Patrol Mode
        item {
            ModeCard(
                number = 1,
                title = "Patrol Mode",
                icon = Icons.Default.DirectionsWalk,
                accentColor = Color(0xFF4CAF50),
                containerColor = Color(0xFFE8F5E9),
                bulletPoints = listOf(
                    "Robot moves autonomously within the defined area",
                    "Continuously monitors surroundings using ultrasonic sensors",
                    "Avoids obstacles using directional decision logic"
                ),
                ledInfo = "Green LED ON" to "(Normal Operation)"
            )
        }

        // 2. Scan Mode
        item {
            ModeCard(
                number = 2,
                title = "Scan Mode",
                icon = Icons.Default.Search,
                accentColor = Color(0xFF2196F3),
                containerColor = Color(0xFFE3F2FD),
                bulletPoints = listOf(
                    "Robot periodically halts",
                    "Performs slight directional adjustments",
                    "Ensures full environmental coverage"
                ),
                ledInfo = null
            )
        }

        // 3. Detection Mode
        item {
            ModeCard(
                number = 3,
                title = "Detection Mode",
                icon = Icons.Default.Notifications,
                accentColor = Color(0xFFFF9800),
                containerColor = Color(0xFFFFF3E0),
                bulletPoints = listOf(
                    "Triggered when abnormal proximity or object presence is detected",
                    "Robot halts and activates camera module"
                ),
                ledInfo = "Blue LED ON" to "(Suspicious Activity)"
            )
        }

        // 4. Threat Verification
        item {
            ModeCard(
                number = 4,
                title = "Threat Verification",
                icon = Icons.Default.VerifiedUser,
                accentColor = Color(0xFF9C27B0),
                containerColor = Color(0xFFF3E5F5),
                bulletPoints = listOf(
                    "Camera captures image for verification",
                    "Reduces false positives through sensor fusion"
                ),
                ledInfo = null
            )
        }

        // 5. Response Mode
        item {
            ModeCard(
                number = 5,
                title = "Response Mode",
                icon = Icons.Default.Warning,
                accentColor = Color(0xFFF44336),
                containerColor = Color(0xFFFFEBEE),
                bulletPoints = listOf("If intrusion confirmed:"),
                subPoints = listOf(
                    "Red LED ON",
                    "Alert signal activated",
                    "Image captured for logging"
                ),
                ledInfo = null
            )
        }

        // bottom spacer
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun ModeCard(
    number: Int,
    title: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    bulletPoints: List<String>,
    subPoints: List<String> = emptyList(),
    ledInfo: Pair<String, String>? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$number. $title",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = accentColor.copy(alpha = 0.2f)
            )

            // Bullet points
            bulletPoints.forEach { point ->
                BulletRow(text = point)
            }

            // Sub-points (indented, used by Response Mode)
            subPoints.forEach { point ->
                Row(
                    modifier = Modifier.padding(start = 32.dp, top = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("○  ", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (point.contains("LED")) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // LED info
            if (ledInfo != null) {
                Spacer(modifier = Modifier.height(4.dp))
                BulletRow(
                    annotatedText = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(ledInfo.first)
                        }
                        append(" ${ledInfo.second}")
                    }
                )
            }
        }
    }
}

@Composable
private fun BulletRow(text: String) {
    Row(
        modifier = Modifier.padding(start = 8.dp, top = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•  ", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun BulletRow(annotatedText: androidx.compose.ui.text.AnnotatedString) {
    Row(
        modifier = Modifier.padding(start = 8.dp, top = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•  ", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        Text(text = annotatedText, style = MaterialTheme.typography.bodyLarge)
    }
}
