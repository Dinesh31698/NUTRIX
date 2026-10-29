package com.nutrix.app.feature.scanresult

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.nutrix.app.core.model.FoodItem
import com.nutrix.app.core.model.MealType

enum class ResultTab {
    NUTRITION,
    ANALYSIS,
    SUGGESTIONS
}

@Composable
fun ScanResultScreen(
    foodItem: FoodItem,
    onAddToToday: (FoodItem, MealType) -> Unit,
    onSaveProduct: (FoodItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(ResultTab.NUTRITION) }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var showMealTypeDialog by remember { mutableStateOf(false) }

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
                    text = "Scan Result",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NutrixColors.BackgroundSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (foodItem.isPackaged) "🥔" else "🍽️",
                            fontSize = 36.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = foodItem.name,
                            color = NutrixColors.TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = foodItem.servingUnit,
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp
                        )
                        if (foodItem.brand != null) {
                            Text(
                                text = foodItem.brand,
                                color = NutrixColors.ElectricBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HealthRatingBadge(
                        rating = foodItem.healthRating,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                ResultTabItem("Nutrition", selectedTab == ResultTab.NUTRITION, Modifier.weight(1f)) {
                    selectedTab = ResultTab.NUTRITION
                }
                ResultTabItem("Analysis", selectedTab == ResultTab.ANALYSIS, Modifier.weight(1f)) {
                    selectedTab = ResultTab.ANALYSIS
                }
                ResultTabItem("Suggestions", selectedTab == ResultTab.SUGGESTIONS, Modifier.weight(1f)) {
                    selectedTab = ResultTab.SUGGESTIONS
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                ResultTab.NUTRITION -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Per Pack (${foodItem.servingUnit})",
                            color = NutrixColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        NutrientDetailRow("Calories", "${foodItem.calories} kcal", Icons.Default.LocalFireDepartment, NutrixColors.CoralRed)
                        NutrientDetailRow("Protein", "${foodItem.protein} g", Icons.Default.FitnessCenter, NutrixColors.MintGreen)
                        NutrientDetailRow("Carbohydrates", "${foodItem.carbs} g", Icons.Default.Grain, NutrixColors.ElectricBlue)
                        NutrientDetailRow("Fats", "${foodItem.fats} g", Icons.Default.Opacity, NutrixColors.WarmAmber)
                        NutrientDetailRow("Fiber", "${foodItem.fiber} g", Icons.Default.Spa, NutrixColors.MintGreen)
                        NutrientDetailRow("Sugar", "${foodItem.sugar} g", Icons.Default.Cookie, NutrixColors.Magenta)
                        NutrientDetailRow("Sodium", "${foodItem.sodium.toInt()} mg", Icons.Default.Shield, NutrixColors.TextSecondary)
                    }
                }
                ResultTab.ANALYSIS -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Nutritional Strengths",
                            color = NutrixColors.MintGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (foodItem.positivePoints.isNotEmpty()) {
                            foodItem.positivePoints.forEach { point ->
                                BulletPoint(text = point, color = NutrixColors.MintGreen)
                            }
                        } else {
                            BulletPoint(text = "Quick source of carbs and sodium for rapid energy", color = NutrixColors.MintGreen)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Potential Concerns",
                            color = NutrixColors.CoralRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (foodItem.concernPoints.isNotEmpty()) {
                            foodItem.concernPoints.forEach { point ->
                                BulletPoint(text = point, color = NutrixColors.CoralRed)
                            }
                        } else {
                            BulletPoint(text = "High in saturated fat from vegetable frying oils", color = NutrixColors.CoralRed)
                            BulletPoint(text = "High sodium density relative to micronutrient content", color = NutrixColors.CoralRed)
                        }

                        if (!foodItem.ingredients.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ingredients",
                                color = NutrixColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = foodItem.ingredients,
                                color = NutrixColors.TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                ResultTab.SUGGESTIONS -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Healthier Smart Alternatives",
                            color = NutrixColors.ElectricBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        AlternativeCard("Roasted Makhana / Fox Nuts", "Rich in fiber, lower calories, 0 trans fats", "3.9 ★")
                        AlternativeCard("Roasted Salted Chickpeas", "High protein (15g), keeps you full for hours", "4.6 ★")
                        AlternativeCard("70% Dark Chocolate", "High in heart-healthy antioxidants and magnesium", "3.8 ★")
                    }
                }
            }
        }

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
                onClick = { onSaveProduct(foodItem) },
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
                onClick = { showMealTypeDialog = true },
                modifier = Modifier.weight(1.3f)
            )
        }

        if (showMealTypeDialog) {
            AlertDialog(
                onDismissRequest = { showMealTypeDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text(
                        text = "Log as which meal?",
                        color = NutrixColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MealType.values().forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedMealType == type) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { selectedMealType = type }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMealType == type,
                                    onClick = { selectedMealType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = type.displayName,
                                    color = NutrixColors.TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    NutrixButton(
                        text = "Confirm Log",
                        onClick = {
                            showMealTypeDialog = false
                            onAddToToday(foodItem, selectedMealType)
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showMealTypeDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ResultTabItem(
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
private fun NutrientDetailRow(
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

@Composable
private fun BulletPoint(text: String, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = "• ", color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = text, color = NutrixColors.TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun AlternativeCard(title: String, desc: String, rating: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = NutrixColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = desc, color = NutrixColors.TextMuted, fontSize = 11.sp)
            }
            Text(text = rating, color = NutrixColors.MintGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}