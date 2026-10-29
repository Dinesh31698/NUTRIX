package com.nutrix.app.feature.fooddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.FoodItem
import com.nutrix.app.core.model.MealType

enum class DetailsTab {
    NUTRITION,
    ANALYSIS,
    SUGGESTIONS
}

@Composable
fun FoodDetailsScreen(
    foodItem: FoodItem,
    onAddToToday: (FoodItem, Float, String, MealType) -> Unit,
    onSave: (FoodItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DetailsTab.NUTRITION) }
    var customGramsInput by remember { mutableStateOf(foodItem.servingSizeGrams.toInt().toString()) }
    var isHomemade by remember { mutableStateOf(foodItem.isHomemade) }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var showMealDialog by remember { mutableStateOf(false) }

    val prepMultiplier = if (isHomemade) 1.0f else 1.15f
    val currentGrams = customGramsInput.toFloatOrNull() ?: foodItem.servingSizeGrams

    val scaled = remember(currentGrams, isHomemade) {
        NutritionCalculator.scaleNutrients(
            baseCalories = (foodItem.calories * prepMultiplier).toInt(),
            baseProtein = foodItem.protein,
            baseCarbs = foodItem.carbs,
            baseFats = foodItem.fats * prepMultiplier,
            baseFiber = foodItem.fiber,
            baseSugar = foodItem.sugar,
            baseSodium = foodItem.sodium * (if (isHomemade) 1f else 1.3f),
            baseGrams = foodItem.servingSizeGrams,
            targetGrams = currentGrams
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
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
                    text = "Food Details",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Food Banner
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        NutrixColors.BackgroundSecondary,
                                        NutrixColors.CardSurface
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = FoodIconHelper.getFoodEmoji(foodItem.name, foodItem.category),
                            fontSize = 64.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = foodItem.name,
                                color = NutrixColors.TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentGrams.toInt()} g portion • ${scaled.calories} kcal",
                                color = NutrixColors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        HealthRatingBadge(
                            rating = foodItem.healthRating,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Portion Control Section (Presets + Custom Grams Input)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Portion Size & Quantity",
                    color = NutrixColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val half = (foodItem.servingSizeGrams * 0.5f).toInt()
                    val full = foodItem.servingSizeGrams.toInt()
                    val double = (foodItem.servingSizeGrams * 1.5f).toInt()

                    PortionPill("0.5x (${half}g)", customGramsInput == half.toString()) {
                        customGramsInput = half.toString()
                    }
                    PortionPill("1x (${full}g)", customGramsInput == full.toString()) {
                        customGramsInput = full.toString()
                    }
                    PortionPill("1.5x (${double}g)", customGramsInput == double.toString()) {
                        customGramsInput = double.toString()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customGramsInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) {
                            customGramsInput = input
                        }
                    },
                    label = { Text("Custom Amount (Grams or ml)", color = NutrixColors.TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NutrixColors.TextPrimary,
                        unfocusedTextColor = NutrixColors.TextPrimary,
                        focusedBorderColor = NutrixColors.ElectricBlue,
                        unfocusedBorderColor = NutrixColors.CardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preparation Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preparation:",
                    color = NutrixColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isHomemade) NutrixColors.MintGreen.copy(alpha = 0.25f) else NutrixColors.CardSurface)
                            .border(1.dp, if (isHomemade) NutrixColors.MintGreen else NutrixColors.CardBorder, RoundedCornerShape(12.dp))
                            .clickable { isHomemade = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Homemade",
                            color = if (isHomemade) NutrixColors.MintGreen else NutrixColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isHomemade) NutrixColors.WarmAmber.copy(alpha = 0.25f) else NutrixColors.CardSurface)
                            .border(1.dp, if (!isHomemade) NutrixColors.WarmAmber else NutrixColors.CardBorder, RoundedCornerShape(12.dp))
                            .clickable { isHomemade = false }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Restaurant",
                            color = if (!isHomemade) NutrixColors.WarmAmber else NutrixColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs Selector
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
                DetailTabPill("Nutrition", selectedTab == DetailsTab.NUTRITION, Modifier.weight(1f)) {
                    selectedTab = DetailsTab.NUTRITION
                }
                DetailTabPill("Analysis", selectedTab == DetailsTab.ANALYSIS, Modifier.weight(1f)) {
                    selectedTab = DetailsTab.ANALYSIS
                }
                DetailTabPill("Suggestions", selectedTab == DetailsTab.SUGGESTIONS, Modifier.weight(1f)) {
                    selectedTab = DetailsTab.SUGGESTIONS
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                DetailsTab.NUTRITION -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailNutrientRow("Calories", "${scaled.calories} kcal", Icons.Default.LocalFireDepartment, NutrixColors.CoralRed)
                        DetailNutrientRow("Protein", "${scaled.protein} g", Icons.Default.FitnessCenter, NutrixColors.MintGreen)
                        DetailNutrientRow("Carbohydrates", "${scaled.carbs} g", Icons.Default.Grain, NutrixColors.ElectricBlue)
                        DetailNutrientRow("Fats", "${scaled.fats} g", Icons.Default.Opacity, NutrixColors.WarmAmber)
                        DetailNutrientRow("Fiber", "${scaled.fiber} g", Icons.Default.Spa, NutrixColors.MintGreen)
                        DetailNutrientRow("Sugar", "${scaled.sugar} g", Icons.Default.Cookie, NutrixColors.Magenta)
                        DetailNutrientRow("Sodium", "${scaled.sodium.toInt()} mg", Icons.Default.Shield, NutrixColors.TextSecondary)
                    }
                }
                DetailsTab.ANALYSIS -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Nutritional Profile", color = NutrixColors.MintGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isHomemade) "Prepared at home with controlled oil and fresh spices." else "Restaurant preparation typically contains 15-20% higher oils and increased sodium for flavor enhancement.",
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Key Micronutrients", color = NutrixColors.ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "• Provides clean dietary fuel to keep blood glucose balanced.", color = NutrixColors.TextPrimary, fontSize = 13.sp)
                        Text(text = "• Traditional whole ingredients provide essential micronutrients and antioxidants.", color = NutrixColors.TextPrimary, fontSize = 13.sp)

                        if (!foodItem.ingredients.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Ingredients", color = NutrixColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = foodItem.ingredients, color = NutrixColors.TextMuted, fontSize = 12.sp)
                        }
                    }
                }
                DetailsTab.SUGGESTIONS -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Pairing Recommendations", color = NutrixColors.ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "• Pair with whole grain chapatis or brown rice for complex carbohydrates and fiber.",
                            color = NutrixColors.TextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Add a bowl of sliced cucumber & tomato salad to blunt post-meal glycemic response.",
                            color = NutrixColors.TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Bottom Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(NutrixColors.BackgroundDark.copy(alpha = 0.95f))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassButton(
                text = "Save",
                onClick = { onSave(foodItem) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = NutrixColors.TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            NutrixButton(
                text = "Add to Today",
                onClick = { showMealDialog = true },
                modifier = Modifier.weight(1.3f)
            )
        }

        // Meal Confirmation Dialog
        if (showMealDialog) {
            AlertDialog(
                onDismissRequest = { showMealDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text(
                        text = "Log ${foodItem.name}",
                        color = NutrixColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Portion: ${currentGrams.toInt()} g (${scaled.calories} kcal, ${scaled.protein}g protein)",
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        MealType.values().forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedMealType == type) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { selectedMealType = type }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMealType == type,
                                    onClick = { selectedMealType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(type.displayName, color = NutrixColors.TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    NutrixButton(
                        text = "Confirm Log",
                        onClick = {
                            showMealDialog = false
                            val loggedFood = foodItem.copy(
                                calories = scaled.calories,
                                protein = scaled.protein,
                                carbs = scaled.carbs,
                                fats = scaled.fats,
                                fiber = scaled.fiber,
                                sugar = scaled.sugar,
                                sodium = scaled.sodium
                            )
                            val portionDesc = "${currentGrams.toInt()} g portion"
                            onAddToToday(loggedFood, currentGrams, portionDesc, selectedMealType)
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showMealDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.PortionPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardSurface)
            .border(1.dp, if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else NutrixColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun DetailTabPill(
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
private fun DetailNutrientRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    color = NutrixColors.TextSecondary,
                    fontSize = 14.sp
                )
            }

            Text(
                text = value,
                color = NutrixColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
