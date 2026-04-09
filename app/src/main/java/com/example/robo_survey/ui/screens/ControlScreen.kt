package com.example.robo_survey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.robo_survey.ui.RobotViewModel

@Composable
fun ControlScreen(viewModel: RobotViewModel) {
    val status by viewModel.robotStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Robot Control",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Operation Mode", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Manual")
                    Switch(
                        checked = status.mode == "Auto",
                        onCheckedChange = { isAuto ->
                            viewModel.setMode(if (isAuto) "Auto" else "Manual")
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Auto")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Directional Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ControlButton(
                icon = Icons.Default.ArrowUpward,
                onClick = { viewModel.sendCommand("FORWARD") },
                enabled = status.mode == "Manual"
            )
            Row {
                ControlButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = { viewModel.sendCommand("LEFT") },
                    enabled = status.mode == "Manual"
                )
                Spacer(modifier = Modifier.width(16.dp))
                ControlButton(
                    icon = Icons.Default.Stop,
                    onClick = { viewModel.sendCommand("STOP") },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    enabled = status.mode == "Manual"
                )
                Spacer(modifier = Modifier.width(16.dp))
                ControlButton(
                    icon = Icons.Default.ArrowForward,
                    onClick = { viewModel.sendCommand("RIGHT") },
                    enabled = status.mode == "Manual"
                )
            }
            ControlButton(
                icon = Icons.Default.ArrowDownward,
                onClick = { viewModel.sendCommand("BACKWARD") },
                enabled = status.mode == "Manual"
            )
        }

        if (status.mode == "Auto") {
            Text(
                text = "Controls disabled in Auto mode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    enabled: Boolean = true
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .padding(8.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor),
        enabled = enabled
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
    }
}
