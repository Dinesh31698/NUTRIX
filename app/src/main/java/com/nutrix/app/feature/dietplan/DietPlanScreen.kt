package com.nutrix.app.feature.dietplan

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
import com.nutrix.app.core.model.DietPlan
import com.nutrix.app.core.model.FoodCategory
import com.nutrix.app.core.model.GoalType
import com.nutrix.app.core.model.MealType
import com.nutrix.app.core.model.UserProfile

@Composable
fun DietPlanScreen(
    userProfile: UserProfile,
    allDietPlans: List<DietPlan>,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onLogMealFromPlan: ((DietPlan) -> Unit)? = null,
    onRegenerate: (Int) -> Unit,
    onGoalChange: (GoalType) -> Unit,
    onBack: () -> Unit
) {
    var selectedWeekOffset by remember { mutableIntStateOf(0) } // 0 = This Week, 1 = Next Week
    var selectedDayOfWeek by remember { mutableIntStateOf(1) } // 1 = Mon .. 7 = Sun
    var showGoalDialog by remember { mutableStateOf(false) }
    var planToConfirmLog by remember { mutableStateOf<DietPlan?>(null) }

    // Filter plans for the selected week and day
    val activePlans = remember(allDietPlans, selectedWeekOffset, selectedDayOfWeek) {
        allDietPlans.filter { it.weekOffset == selectedWeekOffset && it.dayOfWeek == selectedDayOfWeek }
    }

    val dayTotalCalories = activePlans.sumOf { it.calories }
    val dayTotalProtein = activePlans.sumOf { it.protein.toDouble() }.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar
            item {
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
                        text = if (selectedWeekOffset == 0) "My Diet Plan" else "Next Week Plan",
                        color = NutrixColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Goal Header Banner
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
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
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NutrixColors.WarmAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = null,
                                    tint = NutrixColors.WarmAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Your Goal",
                                    color = NutrixColors.TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = userProfile.goalType.title,
                                    color = NutrixColors.TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userProfile.goalType.subtitle,
                                    color = NutrixColors.TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = { showGoalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NutrixColors.ElectricBlue.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Change",
                                color = NutrixColors.ElectricBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Week Toggle (This Week / Next Week)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NutrixColors.CardSurface)
                        .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WeekTabPill("This Week", selectedWeekOffset == 0, Modifier.weight(1f)) {
                        selectedWeekOffset = 0
                    }
                    WeekTabPill("Next Week", selectedWeekOffset == 1, Modifier.weight(1f)) {
                        selectedWeekOffset = 1
                    }
                }
            }

            // Day Selector Pills (Mon - Sun)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon" to 1, "Tue" to 2, "Wed" to 3, "Thu" to 4, "Fri" to 5, "Sat" to 6, "Sun" to 7)
                    days.forEach { (label, dayNum) ->
                        val isSelected = selectedDayOfWeek == dayNum
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardSurface)
                                .border(1.dp, if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardBorder, CircleShape)
                                .clickable { selectedDayOfWeek = dayNum },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else NutrixColors.TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Plan Title & Calorie Target Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val dayName = when (selectedDayOfWeek) {
                            1 -> "Monday"
                            2 -> "Tuesday"
                            3 -> "Wednesday"
                            4 -> "Thursday"
                            5 -> "Friday"
                            6 -> "Saturday"
                            else -> "Sunday"
                        }
                        Text(
                            text = "$dayName's Planned Menu",
                            color = NutrixColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Target: ${userProfile.dailyCalorieTarget} kcal • High Protein",
                            color = NutrixColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total: ~ $dayTotalCalories kcal",
                            color = NutrixColors.ElectricBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Protein: ~ ${dayTotalProtein.toInt()} g",
                            color = NutrixColors.MintGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Meal Cards
            if (activePlans.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No plan generated for this day",
                                color = NutrixColors.TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            NutrixButton(
                                text = "Generate Suggestions",
                                onClick = { onRegenerate(selectedWeekOffset) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                items(activePlans) { plan ->
                    DietMealPlanCard(
                        plan = plan,
                        onToggleCompleted = {
                            if (!plan.isCompleted) {
                                planToConfirmLog = plan
                            } else {
                                onToggleCompleted(plan.id, false)
                            }
                        }
                    )
                }
            }

            // Regenerate / Refresh Suggestions Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                GlassButton(
                    text = "Regenerate Week Suggestions",
                    onClick = { onRegenerate(selectedWeekOffset) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = NutrixColors.TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Confirm Completed & Log Dialog
        if (planToConfirmLog != null) {
            val plan = planToConfirmLog!!
            AlertDialog(
                onDismissRequest = { planToConfirmLog = null },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Complete & Log Meal", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Mark '${plan.foodName}' completed and log it into today's nutrition diary?",
                            color = NutrixColors.TextSecondary,
                            fontSize = 14.sp
                        )
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = NutrixColors.BackgroundSecondary,
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Column {
                                Text(text = plan.portionDesc, color = NutrixColors.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(
                                    text = "${plan.calories} kcal • P: ${plan.protein}g C: ${plan.carbs}g F: ${plan.fats}g",
                                    color = NutrixColors.MintGreen,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    NutrixButton(
                        text = "Confirm & Log",
                        onClick = {
                            val targetPlan = plan
                            planToConfirmLog = null
                            onToggleCompleted(targetPlan.id, true)
                            onLogMealFromPlan?.invoke(targetPlan)
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { planToConfirmLog = null }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Change Goal Dialog
        if (showGoalDialog) {
            AlertDialog(
                onDismissRequest = { showGoalDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Select Nutrition Goal", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GoalType.values().forEach { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (userProfile.goalType == goal) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        onGoalChange(goal)
                                        showGoalDialog = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userProfile.goalType == goal,
                                    onClick = {
                                        onGoalChange(goal)
                                        showGoalDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = goal.title, color = NutrixColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(text = goal.subtitle, color = NutrixColors.TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGoalDialog = false }) {
                        Text("Close", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun WeekTabPill(
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
private fun DietMealPlanCard(
    plan: DietPlan,
    onToggleCompleted: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
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
                // Dish Illustration Thumbnail
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NutrixColors.BackgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = FoodIconHelper.getFoodEmoji(plan.foodName, FoodCategory.CURRIES),
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = plan.mealType.displayName,
                        color = NutrixColors.ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plan.foodName,
                        color = NutrixColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${plan.portionDesc} • ~ ${plan.calories} kcal",
                        color = NutrixColors.TextMuted,
                        fontSize = 12.sp
                    )
                    if (plan.reason.isNotBlank()) {
                        Text(
                            text = plan.reason,
                            color = NutrixColors.MintGreen,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Completion Toggle Checkbox
            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (plan.isCompleted) NutrixColors.MintGreen.copy(alpha = 0.2f) else NutrixColors.CardBorder)
            ) {
                Icon(
                    imageVector = if (plan.isCompleted) Icons.Default.CheckCircle else Icons.Default.Add,
                    contentDescription = null,
                    tint = if (plan.isCompleted) NutrixColors.MintGreen else NutrixColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
