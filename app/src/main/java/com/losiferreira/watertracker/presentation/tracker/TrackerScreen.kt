package com.losiferreira.watertracker.presentation.tracker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import org.koin.androidx.compose.koinViewModel
import com.losiferreira.watertracker.presentation.common.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState(initial = TrackerUiState())
    var customAmount by remember { mutableStateOf(250) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Clean Header
            // Header with settings
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "${uiState.currentMilliliters}ml",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF00BCD4),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "of ${uiState.dailyGoal.goalMilliliters}ml today",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF607D8B),
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center
                    )
                }
                
                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF00BCD4).copy(alpha = 0.7f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Background Fill - stretches to fill available space
            ProgressBackgroundFill(
                progressPercentage = uiState.progressPercentage,
                isGoalAchieved = uiState.isGoalAchieved,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // All Action Buttons
            AllActionButtons(
                onAddWater = { amount ->
                    viewModel.onEvent(TrackerUiEvent.AddWater(amount))
                },
                onRemoveWater = { amount ->
                    viewModel.onEvent(TrackerUiEvent.RemoveWater(amount))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                currentGoal = uiState.dailyGoal.goalMilliliters,
                onDismiss = { showSettingsDialog = false },
                onGoalChanged = { newGoal ->
                    viewModel.onEvent(TrackerUiEvent.UpdateGoal(newGoal))
                    showSettingsDialog = false
                }
            )
        }
    }
}

@Composable
fun ProgressBackgroundFill(
    progressPercentage: Float,
    isGoalAchieved: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    Box(
        modifier = modifier
            .background(
                Color.Black,
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Progress Fill - fills from bottom to top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedProgress)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF87CEEB), // Light blue at top (like sunlight on water)
                                Color(0xFF4FC3F7), // Medium light blue
                                Color(0xFF29B6F6), // Medium blue  
                                Color(0xFF0288D1), // Deeper blue
                                Color(0xFF01579B)  // Deep blue at bottom (water depth)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 400f) // Slightly diagonal
                        )
                    )
            )
        }
        
        // Center Content - always show percentage
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light,
            color = Color.White
        )
    }
}

@Composable
fun AllActionButtons(
    onAddWater: (Int) -> Unit,
    onRemoveWater: (Int) -> Unit
) {
    Column {
        // Add buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(100, 250, 500, 1000).forEach { amount ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            Color(0xFF00BCD4),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onAddWater(amount) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$amount",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Remove buttons - 4 buttons matching the positive amounts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(100, 250, 500, 1000).forEach { amount ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            Color(0xFFFF6B6B),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onRemoveWater(amount) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-$amount",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernRemoveSection(
    onRemoveWater: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remover Água",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(100, 250, 500).forEach { amount ->
                    OutlinedButton(
                        onClick = { onRemoveWater(amount) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFF44336), Color(0xFFE57373))
                            ),
                            width = 2.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "-$amount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                            Text(
                                "ml",
                                fontSize = 10.sp,
                                color = Color(0xFFF44336).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onGoalChanged: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Daily Goal",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Set your daily water intake goal:",
                    color = Color(0xFFB0B0B0),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Goal (ml)", color = Color(0xFF00BCD4)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00BCD4),
                        unfocusedBorderColor = Color(0xFF666666),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00BCD4)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newGoal = goalText.toIntOrNull()
                    if (newGoal != null && newGoal > 0) {
                        onGoalChanged(newGoal)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF00BCD4)
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF666666)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}


