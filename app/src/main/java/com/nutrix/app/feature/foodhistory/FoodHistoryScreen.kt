package com.nutrix.app.feature.foodhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.LoggedMeal
import java.text.SimpleDateFormat
import java.util.*

enum class HistoryRange {
    THIS_WEEK,
    PREVIOUS_WEEKS
}

@Composable
fun FoodHistoryScreen(
    allMeals: List<LoggedMeal>,
    onDeleteMeal: (Long) -> Unit,
    onUpdateMeal: (LoggedMeal) -> Unit = {},
    onBack: () -> Unit
) {
    var selectedRange by remember { mutableStateOf(HistoryRange.THIS_WEEK) }
    var selectedMealForAction by remember { mutableStateOf<LoggedMeal?>(null) }
    var mealToEdit by remember { mutableStateOf<LoggedMeal?>(null) }

    val recent7Days = remember { NutritionCalculator.getRecent7DaysIso() }

    // Filter meals by range
    val filteredMeals = remember(allMeals, selectedRange) {
        if (selectedRange == HistoryRange.THIS_WEEK) {
            allMeals.filter { it.date in recent7Days }
        } else {
            allMeals.filter { it.date !in recent7Days }
        }
    }

    // Group meals by date (YYYY-MM-DD)
    val groupedMeals = remember(filteredMeals) {
        val sorted = filteredMeals.sortedByDescending { it.date }
        sorted.groupBy { it.date }
    }

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.US) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    text = "Food History",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Week Range Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NutrixColors.CardSurface)
                    .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RangePill("This Week", selectedRange == HistoryRange.THIS_WEEK, Modifier.weight(1f)) {
                    selectedRange = HistoryRange.THIS_WEEK
                }
                RangePill("Previous Weeks", selectedRange == HistoryRange.PREVIOUS_WEEKS, Modifier.weight(1f)) {
                    selectedRange = HistoryRange.PREVIOUS_WEEKS
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grouped Meal List
            if (groupedMeals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (selectedRange == HistoryRange.THIS_WEEK) "No meals logged this week" else "No meals in previous weeks",
                            color = NutrixColors.TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Log food from Search or Scan to build your complete nutritional diary.",
                            color = NutrixColors.TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    groupedMeals.forEach { (dateStr, mealsForDate) ->
                        val dayTotalCalories = mealsForDate.sumOf { it.calories }
                        val dayProtein = mealsForDate.sumOf { it.protein.toDouble() }.toFloat()
                        val dayCarbs = mealsForDate.sumOf { it.carbs.toDouble() }.toFloat()
                        val dayFats = mealsForDate.sumOf { it.fats.toDouble() }.toFloat()
                        val displayHeader = NutritionCalculator.formatDisplayDate(dateStr)

                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = NutrixColors.CardSurface.copy(alpha = 0.90f),
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = displayHeader,
                                            color = NutrixColors.TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$dayTotalCalories kcal",
                                            color = NutrixColors.ElectricBlue,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "P: ${dayProtein.toInt()}g • C: ${dayCarbs.toInt()}g • F: ${dayFats.toInt()}g",
                                            color = NutrixColors.TextSecondary,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "${mealsForDate.size} meal(s)",
                                            color = NutrixColors.TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        items(mealsForDate) { meal ->
                            HistoryMealItemCard(
                                meal = meal,
                                timeStr = timeFormat.format(Date(meal.timeMillis)),
                                onClick = { selectedMealForAction = meal }
                            )
                        }
                    }
                }
            }
        }

        // Meal Detail / Action Dialog
        if (selectedMealForAction != null) {
            val meal = selectedMealForAction!!
            AlertDialog(
                onDismissRequest = { selectedMealForAction = null },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = FoodIconHelper.getFoodEmoji(meal.foodName, meal.category),
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = meal.foodName,
                            color = NutrixColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Portion: ${meal.servingDesc}",
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Energy: ${meal.calories} kcal • Protein: ${meal.protein}g",
                            color = NutrixColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Carbs: ${meal.carbs}g • Fats: ${meal.fats}g • Fiber: ${meal.fiber}g",
                            color = NutrixColors.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Logged as ${meal.mealType.displayName} on ${meal.date}",
                            color = NutrixColors.TextMuted,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val targetMeal = selectedMealForAction
                                selectedMealForAction = null
                                mealToEdit = targetMeal
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NutrixColors.ElectricBlue)
                        ) {
                            Text("Edit Portion", color = Color.White)
                        }
                        Button(
                            onClick = {
                                onDeleteMeal(meal.id)
                                selectedMealForAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NutrixColors.CoralRed)
                        ) {
                            Text("Delete", color = Color.White)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedMealForAction = null }) {
                        Text("Close", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Edit Portion Dialog
        if (mealToEdit != null) {
            val meal = mealToEdit!!
            QuantitySelectionDialog(
                foodName = meal.foodName,
                baseServingGrams = meal.servingSizeGrams.coerceAtLeast(100f),
                baseServingDesc = meal.servingDesc,
                baseCalories = meal.calories,
                baseProtein = meal.protein,
                baseCarbs = meal.carbs,
                baseFats = meal.fats,
                onDismiss = { mealToEdit = null },
                onConfirm = { grams, portionDesc, mealType ->
                    val scaled = NutritionCalculator.scaleNutrients(
                        baseCalories = meal.calories,
                        baseProtein = meal.protein,
                        baseCarbs = meal.carbs,
                        baseFats = meal.fats,
                        baseFiber = meal.fiber,
                        baseSugar = meal.sugar,
                        baseSodium = meal.sodium,
                        baseGrams = meal.servingSizeGrams.coerceAtLeast(100f),
                        targetGrams = grams
                    )
                    val updated = meal.copy(
                        servingSizeGrams = grams,
                        servingDesc = portionDesc,
                        calories = scaled.calories,
                        protein = scaled.protein,
                        carbs = scaled.carbs,
                        fats = scaled.fats,
                        mealType = mealType
                    )
                    onUpdateMeal(updated)
                    mealToEdit = null
                }
            )
        }
    }
}

@Composable
private fun RangePill(
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
private fun HistoryMealItemCard(
    meal: LoggedMeal,
    timeStr: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NutrixColors.BackgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = FoodIconHelper.getFoodEmoji(meal.foodName, meal.category),
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = meal.foodName,
                        color = NutrixColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${meal.servingDesc} • ${meal.mealType.displayName}",
                        color = NutrixColors.TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.calories} kcal",
                    color = NutrixColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeStr,
                    color = NutrixColors.TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
