package com.losiferreira.watertracker.presentation.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.presentation.common.collectAsState
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState(initial = HistoryUiState())
    var showMonthDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Clean Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )

                Box {
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF00BCD4).copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { showMonthDropdown = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = uiState.selectedMonth?.let {
                                it.format(DateTimeFormatter.ofPattern("MMM/yyyy"))
                            } ?: "All",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = showMonthDropdown,
                        onDismissRequest = { showMonthDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All months") },
                            onClick = {
                                showMonthDropdown = false
                                viewModel.onEvent(HistoryUiEvent.LoadAllEntries)
                            }
                        )
                        
                        uiState.availableMonths.forEach { month ->
                            DropdownMenuItem(
                                text = { 
                                    Text(month.format(DateTimeFormatter.ofPattern("MMM/yyyy")))
                                },
                                onClick = {
                                    showMonthDropdown = false
                                    viewModel.onEvent(HistoryUiEvent.FilterByMonth(month))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00BCD4),
                            strokeWidth = 3.dp
                        )
                    }
                }
                
                uiState.entries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No records",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFB0B0B0)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start tracking your hydration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF808080)
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.entries) { entry ->
                            CleanHistoryEntry(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CleanHistoryEntry(
    entry: WaterEntry,
    modifier: Modifier = Modifier
) {
    val percentage = (entry.milliliters.toFloat() / 2500f * 100).toInt() // Use hardcoded for now
    val progressColor by animateColorAsState(
        targetValue = when {
            percentage >= 100 -> Color(0xFF0288D1)
            percentage >= 80 -> Color(0xFF29B6F6)
            percentage >= 60 -> Color(0xFF00BCD4)
            percentage >= 40 -> Color(0xFF4FC3F7)
            else -> Color(0xFFB0BEC5)
        }
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.date.format(DateTimeFormatter.ofPattern("MMM dd")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${entry.milliliters}ml",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00BCD4)
                )
                Text(
                    text = "goal: 2500ml", // Hardcoded for now
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
        
        // Horizontal progress bar
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Color(0xFF333333),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF87CEEB), // Light blue at start
                                Color(0xFF4FC3F7), // Medium light blue
                                Color(0xFF29B6F6), // Medium blue  
                                Color(0xFF0288D1), // Deeper blue
                                Color(0xFF01579B)  // Deep blue at end
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(300f, 50f) // Slightly diagonal
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        
        // Clean divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF333333))
        )
    }
}