package com.example.robo_survey.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.robo_survey.data.PathWaypoint
import com.example.robo_survey.ui.RobotViewModel
import kotlin.math.sqrt

@Composable
fun MapScreen(viewModel: RobotViewModel) {
    val waypoints by viewModel.pathWaypoints.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Path Map",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (waypoints.isNotEmpty()) {
                FilledTonalIconButton(onClick = { viewModel.clearPath() }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear path")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val totalDistance = calculateTotalDistance(waypoints)

            MapStatCard(
                label = "Distance",
                value = "%.1f m".format(totalDistance),
                icon = Icons.Default.Straighten,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            MapStatCard(
                label = "Points",
                value = "${waypoints.size}",
                icon = Icons.Default.Place,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            MapStatCard(
                label = "Heading",
                value = if (waypoints.isNotEmpty()) "%.0f°".format(waypoints.last().heading) else "--",
                icon = Icons.Default.Explore,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Map Canvas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if (waypoints.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No path data yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Robot will track its path\nwhen it starts moving",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                PathCanvas(
                    waypoints = waypoints,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFF4CAF50), label = "Start")
            Spacer(modifier = Modifier.width(20.dp))
            LegendItem(color = Color(0xFF2196F3), label = "Path")
            Spacer(modifier = Modifier.width(20.dp))
            LegendItem(color = Color(0xFFF44336), label = "Current")
        }
    }
}

@Composable
private fun PathCanvas(waypoints: List<PathWaypoint>, modifier: Modifier = Modifier) {
    val pathColor = Color(0xFF2196F3)
    val startColor = Color(0xFF4CAF50)
    val endColor = Color(0xFFF44336)
    val gridColor = Color(0xFFE0E0E0)

    Canvas(modifier = modifier) {
        val padding = 40f
        val drawWidth = size.width - 2 * padding
        val drawHeight = size.height - 2 * padding

        // Calculate bounds
        val minX = waypoints.minOf { it.x }
        val maxX = waypoints.maxOf { it.x }
        val minY = waypoints.minOf { it.y }
        val maxY = waypoints.maxOf { it.y }

        val rangeX = (maxX - minX).coerceAtLeast(1f)
        val rangeY = (maxY - minY).coerceAtLeast(1f)
        val scale = minOf(drawWidth / rangeX, drawHeight / rangeY)

        fun toScreen(wp: PathWaypoint): Offset {
            val sx = padding + (wp.x - minX) * scale + (drawWidth - rangeX * scale) / 2
            val sy = padding + (drawHeight - (wp.y - minY) * scale) - (drawHeight - rangeY * scale) / 2
            return Offset(sx, sy)
        }

        // Grid lines
        val gridSpacing = 60f
        var gx = 0f
        while (gx < size.width) {
            drawLine(gridColor, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 0.5f)
            gx += gridSpacing
        }
        var gy = 0f
        while (gy < size.height) {
            drawLine(gridColor, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 0.5f)
            gy += gridSpacing
        }

        // Draw path line
        if (waypoints.size >= 2) {
            val path = Path()
            val first = toScreen(waypoints[0])
            path.moveTo(first.x, first.y)
            waypoints.drop(1).forEach { wp ->
                val pt = toScreen(wp)
                path.lineTo(pt.x, pt.y)
            }
            drawPath(
                path = path,
                color = pathColor,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Draw waypoint dots
        waypoints.forEachIndexed { index, wp ->
            val pt = toScreen(wp)
            val dotColor = when (index) {
                0 -> startColor
                waypoints.lastIndex -> endColor
                else -> pathColor.copy(alpha = 0.5f)
            }
            val radius = when (index) {
                0, waypoints.lastIndex -> 10.dp.toPx()
                else -> 4.dp.toPx()
            }
            drawCircle(dotColor, radius, pt)
            if (index == 0 || index == waypoints.lastIndex) {
                drawCircle(Color.White, radius * 0.5f, pt)
            }
        }
    }
}

@Composable
private fun MapStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun calculateTotalDistance(waypoints: List<PathWaypoint>): Float {
    if (waypoints.size < 2) return 0f
    var total = 0f
    for (i in 1 until waypoints.size) {
        val dx = waypoints[i].x - waypoints[i - 1].x
        val dy = waypoints[i].y - waypoints[i - 1].y
        total += sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
    return total
}
