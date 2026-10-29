package com.nutrix.app.feature.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.LoggedMeal

enum class ProgressRange {
    WEEKLY,
    MONTHLY
}

@Composable
fun ProgressScreen(
    allMeals: List<LoggedMeal>,
    onBack: () -> Unit
) {
    var selectedRange by remember { mutableStateOf(ProgressRange.WEEKLY) }

    val recent7Days = remember { NutritionCalculator.getRecent7DaysIso() }
    val thisWeekMeals = remember(allMeals) { allMeals.filter { it.date in recent7Days } }
    val avgHealthRating = remember(thisWeekMeals) {
        HealthRatingEngine.aggregateDailyHealthRating(thisWeekMeals)
    }

    val calorieValues = listOf(1250f, 1430f, 1380f, 1550f, 1420f, 1600f, 1500f)
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val avgCalories = (calorieValues.average()).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NutrixColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Progress",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Range Toggle: Weekly / Monthly
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NutrixColors.CardSurface)
                    .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProgressPill("Weekly", selectedRange == ProgressRange.WEEKLY, Modifier.weight(1f)) {
                    selectedRange = ProgressRange.WEEKLY
                }
                ProgressPill("Monthly", selectedRange == ProgressRange.MONTHLY, Modifier.weight(1f)) {
                    selectedRange = ProgressRange.MONTHLY
                }
            }

            // Calories Bar Chart Card (Matching Screen 10)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calories",
                            color = NutrixColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Avg",
                                color = NutrixColors.TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "%,d kcal".format(avgCalories),
                                color = NutrixColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BarChart(
                        values = calorieValues,
                        labels = dayLabels,
                        barColor = NutrixColors.MintGreen,
                        maxHeight = 130.dp
                    )
                }
            }

            // Nutrients Donut Card (Matching Screen 10)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Text(
                        text = "Nutrients",
                        color = NutrixColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DonutChart(
                            proteinGrams = 60f,
                            carbsGrams = 180f,
                            fatsGrams = 50f,
                            totalCalories = 1450,
                            size = 120.dp
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MacroLegendItem("Protein", "60 g", NutrixColors.ElectricBlue)
                            MacroLegendItem("Carbs", "180 g", NutrixColors.MintGreen)
                            MacroLegendItem("Fats", "50 g", NutrixColors.WarmAmber)
                        }
                    }
                }
            }

            // Stats Bento Row (Matching Screen 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avg Health Rating
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = NutrixColors.WarmAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Avg. Health Rating",
                                color = NutrixColors.TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = HealthRatingEngine.formatRating(avgHealthRating),
                                color = NutrixColors.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "This Week",
                                color = NutrixColors.TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }

                // Total Foods Logged
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fastfood,
                                contentDescription = null,
                                tint = NutrixColors.ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Total Foods",
                                color = NutrixColors.TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${thisWeekMeals.size}",
                                color = NutrixColors.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Items Logged",
                                color = NutrixColors.TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NutrixColors.ElectricBlue else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else NutrixColors.TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun MacroLegendItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = NutrixColors.TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = value, color = NutrixColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
