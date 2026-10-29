package com.nutrix.app.core.designsystem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.MealType
import kotlin.math.min

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color = NutrixColors.CardSurface.copy(alpha = 0.80f),
    borderColor: Color = NutrixColors.CardBorder,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(clickModifier)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NutrixColors.ElectricBlue.copy(alpha = 0.15f),
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, NutrixColors.CardBorder, shape)
            .then(clickModifier),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = NutrixColors.CardSurface.copy(alpha = 0.88f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun NutrixButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    gradient: Brush = NutrixColors.PrimaryGradient
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = NutrixColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon.invoke()
            }
        }
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    borderColor: Color = NutrixColors.CardBorder
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = NutrixColors.CardSurface.copy(alpha = 0.60f),
            contentColor = NutrixColors.TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.invoke()
            if (icon != null) Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = NutrixColors.TextPrimary
            )
        }
    }
}

@Composable
fun NutrixSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search food, fruit, biscuit, chocolate or curry...",
    onSearch: () -> Unit = {},
    onMicClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(NutrixColors.CardSurface.copy(alpha = 0.90f))
            .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(27.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = NutrixColors.ElectricBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = NutrixColors.TextPrimary,
                    fontSize = 15.sp
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = NutrixColors.TextMuted,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = NutrixColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (onMicClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = NutrixColors.ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
    showMax: Boolean = true
) {
    val (ratingColor, ratingText) = when {
        rating >= 4.0 -> NutrixColors.MintGreen to String.format("%.1f", rating)
        rating >= 3.0 -> NutrixColors.ElectricBlue to String.format("%.1f", rating)
        rating >= 2.0 -> NutrixColors.WarmAmber to String.format("%.1f", rating)
        else -> NutrixColors.CoralRed to String.format("%.1f", rating)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(NutrixColors.CardSurface)
            .border(1.5.dp, ratingColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = ratingText,
                color = ratingColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            if (showMax) {
                Text(
                    text = "/5",
                    color = NutrixColors.TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NutritionProgressRing(
    consumedCalories: Int,
    targetCalories: Int,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    strokeWidth: Dp = 12.dp
) {
    val progress = if (targetCalories > 0) {
        min(consumedCalories.toFloat() / targetCalories.toFloat(), 1.0f)
    } else 0f

    val remaining = targetCalories - consumedCalories

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900),
        label = "progress_ring"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = min(this.size.width, this.size.height) - strokePx
            val topLeft = Offset(
                (this.size.width - diameter) / 2,
                (this.size.height - diameter) / 2
            )
            val arcSize = Size(diameter, diameter)

            // Background Track
            drawArc(
                color = NutrixColors.CardBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Progress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        NutrixColors.ElectricBlue,
                        NutrixColors.MintGreen,
                        NutrixColors.ElectricBlue
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "%,d".format(consumedCalories),
                color = NutrixColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "/ %,d kcal".format(targetCalories),
                color = NutrixColors.TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (remaining >= 0) "$remaining left" else "${-remaining} over",
                color = if (remaining >= 0) NutrixColors.MintGreen else NutrixColors.CoralRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NutrientProgressBar(
    label: String,
    current: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) min(current.toFloat() / target.toFloat(), 1.0f) else 0f

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = NutrixColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "$current / $target g",
                color = NutrixColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(NutrixColors.CardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun DonutChart(
    proteinGrams: Float,
    carbsGrams: Float,
    fatsGrams: Float,
    totalCalories: Int,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    strokeWidth: Dp = 18.dp
) {
    val total = (proteinGrams * 4 + carbsGrams * 4 + fatsGrams * 9).coerceAtLeast(1f)
    val proteinAngle = (proteinGrams * 4 / total) * 360f
    val carbsAngle = (carbsGrams * 4 / total) * 360f
    val fatsAngle = (fatsGrams * 9 / total) * 360f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = min(this.size.width, this.size.height) - strokePx
            val topLeft = Offset((this.size.width - diameter) / 2, (this.size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            var startAngle = -90f

            // Protein
            drawArc(
                color = NutrixColors.ElectricBlue,
                startAngle = startAngle,
                sweepAngle = proteinAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt)
            )
            startAngle += proteinAngle

            // Carbs
            drawArc(
                color = NutrixColors.MintGreen,
                startAngle = startAngle,
                sweepAngle = carbsAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt)
            )
            startAngle += carbsAngle

            // Fats
            drawArc(
                color = NutrixColors.WarmAmber,
                startAngle = startAngle,
                sweepAngle = fatsAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%,d".format(totalCalories),
                color = NutrixColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "kcal",
                color = NutrixColors.TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = NutrixColors.MintGreen,
    maxHeight: Dp = 140.dp
) {
    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(100f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { index, value ->
            val fraction = (value / maxValue).coerceIn(0.08f, 1.0f)
            val label = labels.getOrElse(index) { "" }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(fraction)
                        .width(18.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(barColor, barColor.copy(alpha = 0.6f))
                            )
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    color = NutrixColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuantitySelectionDialog(
    foodName: String,
    baseServingGrams: Float,
    baseServingDesc: String,
    baseCalories: Int,
    baseProtein: Float,
    baseCarbs: Float,
    baseFats: Float,
    onDismiss: () -> Unit,
    onConfirm: (grams: Float, portionDesc: String, mealType: MealType) -> Unit
) {
    var quantityInput by remember { mutableStateOf(baseServingGrams.toInt().toString()) }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }

    val currentGrams = quantityInput.toFloatOrNull() ?: 0f
    val isInputValid = currentGrams > 0f

    val scaled = remember(currentGrams) {
        NutritionCalculator.scaleNutrients(
            baseCalories = baseCalories,
            baseProtein = baseProtein,
            baseCarbs = baseCarbs,
            baseFats = baseFats,
            baseFiber = 0f,
            baseSugar = 0f,
            baseSodium = 0f,
            baseGrams = baseServingGrams,
            targetGrams = currentGrams
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NutrixColors.CardSurface,
        title = {
            Text(
                text = "Log $foodName",
                color = NutrixColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Preset Quick Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(50, 100, 150, 200, 300)
                    presets.forEach { presetGrams ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (quantityInput == presetGrams.toString()) NutrixColors.ElectricBlue else NutrixColors.BackgroundSecondary)
                                .clickable { quantityInput = presetGrams.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${presetGrams}g",
                                color = if (quantityInput == presetGrams.toString()) Color.White else NutrixColors.TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Custom Grams Numeric Input
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) {
                            quantityInput = input
                        }
                    },
                    label = { Text("Quantity in Grams (g) or ml", color = NutrixColors.TextSecondary) },
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

                // Live Nutrition Breakdown
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NutrixColors.BackgroundSecondary,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${scaled.calories}", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("kcal", color = NutrixColors.TextMuted, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${scaled.protein}g", color = NutrixColors.MintGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("protein", color = NutrixColors.TextMuted, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${scaled.carbs}g", color = NutrixColors.ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("carbs", color = NutrixColors.TextMuted, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${scaled.fats}g", color = NutrixColors.WarmAmber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("fats", color = NutrixColors.TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                // Meal Type Selector
                Text("Select Meal:", color = NutrixColors.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                MealType.values().forEach { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedMealType == meal) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { selectedMealType = meal }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMealType == meal,
                            onClick = { selectedMealType = meal },
                            colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(meal.displayName, color = NutrixColors.TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            NutrixButton(
                text = "Confirm Log",
                enabled = isInputValid,
                onClick = {
                    val portionDesc = "${currentGrams.toInt()} g portion"
                    onConfirm(currentGrams, portionDesc, selectedMealType)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NutrixColors.TextSecondary)
            }
        }
    )
}
