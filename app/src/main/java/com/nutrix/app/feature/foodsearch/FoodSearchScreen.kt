package com.nutrix.app.feature.foodsearch

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.FoodCategory
import com.nutrix.app.core.model.FoodItem
import com.nutrix.app.core.model.MealType
import java.util.Locale

@Composable
fun FoodSearchScreen(
    foods: List<FoodItem>,
    onFoodClick: (FoodItem) -> Unit,
    onQuickAdd: (FoodItem, Float, String, MealType) -> Unit,
    onAddCustomFood: ((FoodItem) -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.ALL) }
    var quickAddTargetFood by remember { mutableStateOf<FoodItem?>(null) }
    var showCustomFoodDialog by remember { mutableStateOf(false) }

    // Speech Recognition Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                searchQuery = spokenText
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a food or dish name (e.g. Chicken Curry, Dosa)...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Voice recognition service unavailable", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission required for voice search", Toast.LENGTH_SHORT).show()
        }
    }

    fun startVoiceSearch() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a food or dish name...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Voice search unavailable on this device", Toast.LENGTH_SHORT).show()
            }
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val filteredFoods = remember(searchQuery, selectedCategory, foods) {
        foods.filter { item ->
            val matchesCategory = (selectedCategory == FoodCategory.ALL) || (item.category == selectedCategory)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) ||
                (item.brand?.contains(searchQuery, ignoreCase = true) == true)
            }
            matchesCategory && matchesQuery
        }
    }

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
            // Header Top Bar with Search & Mic
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

                Spacer(modifier = Modifier.width(12.dp))

                NutrixSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search food, curry, fruit...",
                    modifier = Modifier.weight(1f),
                    onMicClick = { startVoiceSearch() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories Filter Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf(
                    FoodCategory.ALL,
                    FoodCategory.CURRIES,
                    FoodCategory.RICE_ITEMS,
                    FoodCategory.GRAINS,
                    FoodCategory.PROTEIN,
                    FoodCategory.DAIRY,
                    FoodCategory.FRUITS,
                    FoodCategory.VEGETABLES,
                    FoodCategory.BEVERAGES,
                    FoodCategory.SNACKS,
                    FoodCategory.PACKAGED
                )
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) NutrixColors.VividViolet else NutrixColors.CardSurface)
                            .border(1.dp, if (isSelected) NutrixColors.Magenta else NutrixColors.CardBorder, RoundedCornerShape(18.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category.displayName,
                            color = NutrixColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Food Count & Add Custom Food Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredFoods.size} foods available",
                    color = NutrixColors.TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "+ Add Custom Food",
                    color = NutrixColors.ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showCustomFoodDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredFoods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔍 No food found for '$searchQuery'",
                            color = NutrixColors.TextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try searching for 'Chicken', 'Biryani', 'Dosa', 'Paneer', or tap below to add a custom dish.",
                            color = NutrixColors.TextMuted,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NutrixButton(
                            text = "Create Custom Dish",
                            onClick = { showCustomFoodDialog = true }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFoods) { food ->
                        FoodSearchResultCard(
                            food = food,
                            onClick = { onFoodClick(food) },
                            onAddClick = { quickAddTargetFood = food }
                        )
                    }
                }
            }
        }

        // Mandatory Quantity Input Dialog
        if (quickAddTargetFood != null) {
            val target = quickAddTargetFood!!
            QuantitySelectionDialog(
                foodName = target.name,
                baseServingGrams = target.servingSizeGrams,
                baseServingDesc = target.servingUnit,
                baseCalories = target.calories,
                baseProtein = target.protein,
                baseCarbs = target.carbs,
                baseFats = target.fats,
                onDismiss = { quickAddTargetFood = null },
                onConfirm = { grams, portionDesc, mealType ->
                    quickAddTargetFood = null
                    onQuickAdd(target, grams, portionDesc, mealType)
                }
            )
        }

        // Add Custom Food Dialog
        if (showCustomFoodDialog) {
            AddCustomFoodDialog(
                initialName = searchQuery,
                onDismiss = { showCustomFoodDialog = false },
                onSave = { newFood ->
                    showCustomFoodDialog = false
                    onAddCustomFood?.invoke(newFood)
                }
            )
        }
    }
}

@Composable
private fun FoodSearchResultCard(
    food: FoodItem,
    onClick: () -> Unit,
    onAddClick: () -> Unit
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
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NutrixColors.BackgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = FoodIconHelper.getFoodEmoji(food.name, food.category),
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = food.name,
                        color = NutrixColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${food.servingUnit} • P: ${food.protein}g C: ${food.carbs}g F: ${food.fats}g",
                        color = NutrixColors.TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = NutrixColors.WarmAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", food.healthRating),
                            color = NutrixColors.WarmAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${food.calories} kcal",
                            color = NutrixColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NutrixColors.ElectricBlue.copy(alpha = 0.2f))
                    .border(1.dp, NutrixColors.ElectricBlue.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to meal",
                    tint = NutrixColors.ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddCustomFoodDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onSave: (FoodItem) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var calories by remember { mutableStateOf("250") }
    var protein by remember { mutableStateOf("10") }
    var carbs by remember { mutableStateOf("30") }
    var fats by remember { mutableStateOf("8") }
    var grams by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NutrixColors.CardSurface,
        title = {
            Text("Add Custom Food", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food / Dish Name", color = NutrixColors.TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NutrixColors.TextPrimary,
                        unfocusedTextColor = NutrixColors.TextPrimary,
                        focusedBorderColor = NutrixColors.ElectricBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("Calories (kcal)", color = NutrixColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = grams,
                        onValueChange = { grams = it },
                        label = { Text("Serving (g)", color = NutrixColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)", color = NutrixColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carbs (g)", color = NutrixColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it },
                        label = { Text("Fats (g)", color = NutrixColors.TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            NutrixButton(
                text = "Save Food",
                enabled = name.isNotBlank(),
                onClick = {
                    val cal = calories.toIntOrNull() ?: 200
                    val prot = protein.toFloatOrNull() ?: 5f
                    val carb = carbs.toFloatOrNull() ?: 25f
                    val fat = fats.toFloatOrNull() ?: 5f
                    val servingGrams = grams.toFloatOrNull() ?: 100f

                    val calculatedRating = HealthRatingEngine.calculateRating(
                        category = FoodCategory.ALL,
                        calories = cal,
                        proteinGrams = prot,
                        carbsGrams = carb,
                        fatsGrams = fat,
                        fiberGrams = 2f,
                        sugarGrams = 2f,
                        sodiumMg = 50f,
                        servingGrams = servingGrams,
                        isPackaged = false
                    ).score

                    val newFood = FoodItem(
                        name = name.trim(),
                        category = FoodCategory.ALL,
                        servingSizeGrams = servingGrams,
                        servingUnit = "${servingGrams.toInt()} g",
                        calories = cal,
                        protein = prot,
                        carbs = carb,
                        fats = fat,
                        healthRating = calculatedRating,
                        isHomemade = true
                    )
                    onSave(newFood)
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
