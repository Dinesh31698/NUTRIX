package com.nutrix.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.model.DayNutritionSummary
import com.nutrix.app.core.model.DietPlan
import com.nutrix.app.core.model.LoggedMeal
import com.nutrix.app.core.model.UserProfile
import java.util.Locale

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    todaySummary: DayNutritionSummary,
    waterGlasses: Int,
    nextPlannedMeal: DietPlan?,
    onWaterChange: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToDietPlan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMealDetails: (Long) -> Unit
) {
    var showWaterDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }

    // Dynamic macro targets calculated from user daily calorie target
    val targetCalories = userProfile.dailyCalorieTarget
    val proteinTargetGrams = (targetCalories * 0.25f / 4f).toInt().coerceAtLeast(50)
    val carbsTargetGrams = (targetCalories * 0.50f / 4f).toInt().coerceAtLeast(100)
    val fatsTargetGrams = (targetCalories * 0.25f / 9f).toInt().coerceAtLeast(30)

    val currentRating = todaySummary.avgRating

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header: Greeting & Profile
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onNavigateToProfile)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(NutrixColors.CardSurface)
                                .border(1.5.dp, NutrixColors.ElectricBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(1).uppercase(),
                                color = NutrixColors.ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hello, ${userProfile.name} ☀️",
                                color = NutrixColors.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Track healthy & stay energized!",
                                color = NutrixColors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NutrixColors.CardSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = NutrixColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Search Bar
            item {
                NutrixSearchBar(
                    query = "",
                    onQueryChange = {},
                    placeholder = "Search food, fruit, biscuit, chocolate or curry...",
                    modifier = Modifier.clickable(onClick = onNavigateToSearch),
                    onMicClick = onNavigateToSearch
                )
            }

            // 4 Top Quick Navigation Pills
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionIconPill("Scan Food", Icons.Default.QrCodeScanner, NutrixColors.ElectricBlue, onNavigateToScanner)
                    ActionIconPill("Search Food", Icons.Default.Search, NutrixColors.WarmAmber, onNavigateToSearch)
                    ActionIconPill("Diet Plan", Icons.Default.CalendarToday, NutrixColors.MintGreen, onNavigateToDietPlan)
                    ActionIconPill("History", Icons.Default.History, NutrixColors.Magenta, onNavigateToHistory)
                }
            }

            // Today's Nutrition Dashboard Card
            item {
                Text(
                    text = "Today's Summary",
                    color = NutrixColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NutritionProgressRing(
                                consumedCalories = todaySummary.totalCalories,
                                targetCalories = targetCalories,
                                size = 130.dp,
                                strokeWidth = 11.dp
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NutrientProgressBar(
                                    label = "Protein",
                                    current = todaySummary.proteinGrams.toInt(),
                                    target = proteinTargetGrams,
                                    color = NutrixColors.ElectricBlue
                                )
                                NutrientProgressBar(
                                    label = "Carbs",
                                    current = todaySummary.carbsGrams.toInt(),
                                    target = carbsTargetGrams,
                                    color = NutrixColors.MintGreen
                                )
                                NutrientProgressBar(
                                    label = "Fats",
                                    current = todaySummary.fatsGrams.toInt(),
                                    target = fatsTargetGrams,
                                    color = NutrixColors.WarmAmber
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(NutrixColors.CardBorder)
                        )

                        // Water Hydration and Health Rating Interactive Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Water Card (Clickable to dialog)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showWaterDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Water",
                                    tint = NutrixColors.ElectricBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Water",
                                        color = NutrixColors.TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "$waterGlasses / ${userProfile.dailyWaterGlasses} glasses",
                                        color = NutrixColors.TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onWaterChange((waterGlasses - 1).coerceAtLeast(0)) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("-", color = NutrixColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { onWaterChange(waterGlasses + 1) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("+", color = NutrixColors.ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Health Rating Badge (Clickable to Insights)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showRatingDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = if (currentRating != null) NutrixColors.WarmAmber else NutrixColors.TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Health Rating",
                                        color = NutrixColors.TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (currentRating != null) {
                                            "${String.format(Locale.US, "%.1f", currentRating)} ★ Today"
                                        } else {
                                            "— No rated foods"
                                        },
                                        color = NutrixColors.TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions 2x2 Bento Grid
            item {
                Text(
                    text = "Quick Actions",
                    color = NutrixColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BentoActionCard(
                        title = "Scan\nPackaged Food",
                        icon = Icons.Default.CropFree,
                        glowColor = NutrixColors.Magenta,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToScanner
                    )
                    BentoActionCard(
                        title = "Search\nFood / Dish",
                        icon = Icons.Default.Search,
                        glowColor = NutrixColors.WarmAmber,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSearch
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BentoActionCard(
                        title = "My Diet\nPlan",
                        icon = Icons.Default.Assignment,
                        glowColor = NutrixColors.MintGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDietPlan
                    )
                    BentoActionCard(
                        title = "Food\nHistory",
                        icon = Icons.Default.History,
                        glowColor = NutrixColors.ElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )
                }
            }

            // Recent Meals Today
            if (todaySummary.meals.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Meals Today",
                        color = NutrixColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        todaySummary.meals.take(5).forEach { meal ->
                            RecentMealRow(meal = meal, onClick = { onNavigateToMealDetails(meal.foodId) })
                        }
                    }
                }
            }

            // Next Planned Meal
            if (nextPlannedMeal != null) {
                item {
                    Text(
                        text = "Next Planned Meal",
                        color = NutrixColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = NutrixColors.ElectricBlue.copy(alpha = 0.5f),
                        onClick = onNavigateToDietPlan
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nextPlannedMeal.mealType.displayName.uppercase(),
                                    color = NutrixColors.ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = nextPlannedMeal.foodName,
                                    color = NutrixColors.TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${nextPlannedMeal.portionDesc} • ~ ${nextPlannedMeal.calories} kcal",
                                    color = NutrixColors.TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = NutrixColors.ElectricBlue
                            )
                        }
                    }
                }
            }
        }

        // Water Hydration Dialog
        if (showWaterDialog) {
            AlertDialog(
                onDismissRequest = { showWaterDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Daily Water Hydration", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "You have logged $waterGlasses of ${userProfile.dailyWaterGlasses} glasses today (${waterGlasses * 250} ml).",
                            color = NutrixColors.TextSecondary,
                            fontSize = 14.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onWaterChange(waterGlasses + 1) },
                                colors = ButtonDefaults.buttonColors(containerColor = NutrixColors.ElectricBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+1 Glass", color = Color.White)
                            }
                            Button(
                                onClick = { onWaterChange(waterGlasses + 2) },
                                colors = ButtonDefaults.buttonColors(containerColor = NutrixColors.ElectricBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+2 (500ml)", color = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showWaterDialog = false }) {
                        Text("Done", color = NutrixColors.ElectricBlue)
                    }
                }
            )
        }

        // Health Rating Insights Dialog
        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Today's Health Rating", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (currentRating != null) {
                                "Average Score: ${String.format(Locale.US, "%.1f", currentRating)} / 5.0"
                            } else {
                                "No rated foods logged today"
                            },
                            color = if (currentRating != null) NutrixColors.MintGreen else NutrixColors.TextMuted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentRating != null) {
                                when {
                                    currentRating >= 4.0 -> "Your food choices today are highly balanced with excellent whole-food nutrition and low refined sugar."
                                    currentRating >= 3.0 -> "Moderate nutritional balance. Consider adding more leafy greens, fresh fruits, or lean protein."
                                    else -> "Higher concentration of processed or high-sodium/sugar choices. Try swapping for whole food alternatives."
                                }
                            } else {
                                "Log your meals today to see your dynamic health score, whole-food quality rating, and nutrition insights."
                            },
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Wholesome dishes like Dal, Fruits, and Salads boost your score.\n• Ultra-processed items like sugary sodas and packaged chips decrease your score.",
                            color = NutrixColors.TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRatingDialog = false }) {
                        Text("Understood", color = NutrixColors.ElectricBlue)
                    }
                }
            )
        }
    }
}

@Composable
private fun ActionIconPill(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(NutrixColors.CardSurface)
                .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = NutrixColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BentoActionCard(
    title: String,
    icon: ImageVector,
    glowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NutrixColors.CardSurface)
            .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(glowColor.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                color = NutrixColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun RecentMealRow(
    meal: LoggedMeal,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NutrixColors.BackgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = FoodIconHelper.getFoodEmoji(meal.foodName, meal.category),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = meal.foodName,
                        color = NutrixColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${meal.servingDesc} • ${meal.mealType.displayName}",
                        color = NutrixColors.TextMuted,
                        fontSize = 11.sp
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
                    text = String.format("%.1f ★", meal.rating),
                    color = NutrixColors.MintGreen,
                    fontSize = 11.sp
                )
            }
        }
    }
}
