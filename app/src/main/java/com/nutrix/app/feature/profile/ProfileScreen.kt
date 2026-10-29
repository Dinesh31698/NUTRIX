package com.nutrix.app.feature.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.DietaryPreference
import com.nutrix.app.core.model.GoalType
import com.nutrix.app.core.model.UserProfile

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToGrocery: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onBack: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showPreferencesDialog by remember { mutableStateOf(false) }
    var showAllergiesDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showOfflineDialog by remember { mutableStateOf(false) }

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
                    text = "Profile & Settings",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // User Profile Header Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(NutrixColors.CardSurface)
                                .border(2.dp, NutrixColors.ElectricBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(1).uppercase(),
                                color = NutrixColors.ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = userProfile.name,
                                color = NutrixColors.TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${userProfile.weightKg.toInt()} kg • ${userProfile.heightCm.toInt()} cm • ${userProfile.age} yrs",
                                color = NutrixColors.TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Target: ${userProfile.dailyCalorieTarget} kcal/day",
                                color = NutrixColors.MintGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = NutrixColors.ElectricBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Options List
            ProfileOptionRow(
                title = "My Goals",
                subtitle = userProfile.goalType.title,
                icon = Icons.Default.TrackChanges,
                iconColor = NutrixColors.WarmAmber,
                onClick = { showGoalDialog = true }
            )

            ProfileOptionRow(
                title = "My Preferences",
                subtitle = "${userProfile.dietaryPreference.label}, Indian Food",
                icon = Icons.Default.Restaurant,
                iconColor = NutrixColors.Magenta,
                onClick = { showPreferencesDialog = true }
            )

            ProfileOptionRow(
                title = "Allergies & Restrictions",
                subtitle = if (userProfile.allergies.isNotEmpty()) userProfile.allergies.joinToString(", ") else "No allergies added (Safe)",
                icon = Icons.Default.Shield,
                iconColor = NutrixColors.CoralRed,
                onClick = { showAllergiesDialog = true }
            )

            ProfileOptionRow(
                title = "Reminder Settings",
                subtitle = "Meal & Water notifications",
                icon = Icons.Default.Notifications,
                iconColor = NutrixColors.MintGreen,
                onClick = onNavigateToReminders
            )

            ProfileOptionRow(
                title = "Grocery Shopping List",
                subtitle = "Ingredients from weekly diet plan",
                icon = Icons.Default.ShoppingCart,
                iconColor = NutrixColors.ElectricBlue,
                onClick = onNavigateToGrocery
            )

            ProfileOptionRow(
                title = "AI Food Assistant",
                subtitle = "Local offline nutrition intelligence",
                icon = Icons.Default.AutoAwesome,
                iconColor = NutrixColors.VividViolet,
                onClick = onNavigateToAssistant
            )

            ProfileOptionRow(
                title = "Language",
                subtitle = when (userProfile.language) {
                    "te" -> "తెలుగు (Telugu)"
                    "hi" -> "हिन्दी (Hindi)"
                    else -> "English"
                },
                icon = Icons.Default.Language,
                iconColor = NutrixColors.ElectricBlue,
                onClick = { showLanguageDialog = true }
            )

            ProfileOptionRow(
                title = "Offline Mode Status",
                subtitle = "Local SQLite active • 118 foods stored",
                icon = Icons.Default.CloudDone,
                iconColor = NutrixColors.MintGreen,
                onClick = { showOfflineDialog = true }
            )
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            EditProfileDialog(
                current = userProfile,
                onDismiss = { showEditProfileDialog = false },
                onSave = { updated ->
                    showEditProfileDialog = false
                    onUpdateProfile(updated)
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
                                        val newTarget = NutritionCalculator.calculateTargetCalories(
                                            weightKg = userProfile.weightKg,
                                            heightCm = userProfile.heightCm,
                                            age = userProfile.age,
                                            goal = goal
                                        )
                                        onUpdateProfile(userProfile.copy(goalType = goal, dailyCalorieTarget = newTarget))
                                        showGoalDialog = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userProfile.goalType == goal,
                                    onClick = {
                                        val newTarget = NutritionCalculator.calculateTargetCalories(
                                            weightKg = userProfile.weightKg,
                                            heightCm = userProfile.heightCm,
                                            age = userProfile.age,
                                            goal = goal
                                        )
                                        onUpdateProfile(userProfile.copy(goalType = goal, dailyCalorieTarget = newTarget))
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
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Dietary Preferences Dialog
        if (showPreferencesDialog) {
            AlertDialog(
                onDismissRequest = { showPreferencesDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Dietary Preference", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DietaryPreference.values().forEach { pref ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (userProfile.dietaryPreference == pref) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        onUpdateProfile(userProfile.copy(dietaryPreference = pref))
                                        showPreferencesDialog = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userProfile.dietaryPreference == pref,
                                    onClick = {
                                        onUpdateProfile(userProfile.copy(dietaryPreference = pref))
                                        showPreferencesDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(pref.label, color = NutrixColors.TextPrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPreferencesDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Allergies & Restrictions Multi-select Dialog
        if (showAllergiesDialog) {
            val commonAllergens = listOf("Dairy", "Gluten", "Peanuts", "Tree Nuts", "Soy", "Eggs", "Shellfish")
            var selectedAllergies by remember { mutableStateOf(userProfile.allergies.toSet()) }

            AlertDialog(
                onDismissRequest = { showAllergiesDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Allergies & Restrictions", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Select ingredients to flag or avoid:", color = NutrixColors.TextSecondary, fontSize = 12.sp)
                        commonAllergens.forEach { allergen ->
                            val isChecked = selectedAllergies.contains(allergen)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedAllergies = if (isChecked) {
                                            selectedAllergies - allergen
                                        } else {
                                            selectedAllergies + allergen
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedAllergies = if (checked) selectedAllergies + allergen else selectedAllergies - allergen
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = NutrixColors.CoralRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(allergen, color = NutrixColors.TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    NutrixButton(
                        text = "Save Allergies",
                        onClick = {
                            onUpdateProfile(userProfile.copy(allergies = selectedAllergies.toList()))
                            showAllergiesDialog = false
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showAllergiesDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Language Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Text("Select App Language", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("en" to "English", "te" to "తెలుగు (Telugu)", "hi" to "हिन्दी (Hindi)").forEach { (code, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (userProfile.language == code) NutrixColors.ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        onUpdateProfile(userProfile.copy(language = code))
                                        showLanguageDialog = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userProfile.language == code,
                                    onClick = {
                                        onUpdateProfile(userProfile.copy(language = code))
                                        showLanguageDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = NutrixColors.ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = label, color = NutrixColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }

        // Offline Mode Status Dialog
        if (showOfflineDialog) {
            AlertDialog(
                onDismissRequest = { showOfflineDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = NutrixColors.MintGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Offline Engine Status", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Local Database: 100% Operational & Persistent (Room SQLite)", color = NutrixColors.MintGreen, fontSize = 13.sp)
                        Text("• Food Catalogue: 118+ comprehensive nutritional records bundled locally", color = NutrixColors.TextPrimary, fontSize = 13.sp)
                        Text("• AI Assistant: Local deterministic natural language intelligence engine (zero internet required)", color = NutrixColors.TextPrimary, fontSize = 13.sp)
                        Text("• Barcode Scanner: Bundled ML Kit barcode model works without internet", color = NutrixColors.TextPrimary, fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    NutrixButton(text = "Awesome", onClick = { showOfflineDialog = false })
                }
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    current: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(current.name) }
    var age by remember { mutableStateOf(current.age.toString()) }
    var height by remember { mutableStateOf(current.heightCm.toInt().toString()) }
    var weight by remember { mutableStateOf(current.weightKg.toInt().toString()) }
    var calories by remember { mutableStateOf(current.dailyCalorieTarget.toString()) }
    var water by remember { mutableStateOf(current.dailyWaterGlasses.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NutrixColors.CardSurface,
        title = {
            Text("Edit User Profile", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = NutrixColors.TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
                        label = { Text("Age (yrs)", color = NutrixColors.TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { if (it.all { c -> c.isDigit() }) height = it },
                        label = { Text("Height (cm)", color = NutrixColors.TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { if (it.all { c -> c.isDigit() }) weight = it },
                        label = { Text("Weight (kg)", color = NutrixColors.TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = water,
                        onValueChange = { if (it.all { c -> c.isDigit() }) water = it },
                        label = { Text("Water (glasses)", color = NutrixColors.TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.all { c -> c.isDigit() }) calories = it },
                    label = { Text("Daily Calorie Target (kcal)", color = NutrixColors.TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NutrixColors.TextPrimary, unfocusedTextColor = NutrixColors.TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            NutrixButton(
                text = "Save Profile",
                onClick = {
                    val a = age.toIntOrNull() ?: current.age
                    val h = height.toFloatOrNull() ?: current.heightCm
                    val w = weight.toFloatOrNull() ?: current.weightKg
                    val cal = calories.toIntOrNull() ?: current.dailyCalorieTarget
                    val wat = water.toIntOrNull() ?: current.dailyWaterGlasses

                    onSave(
                        current.copy(
                            name = name.ifBlank { "User" },
                            age = a,
                            heightCm = h,
                            weightKg = w,
                            dailyCalorieTarget = cal,
                            dailyWaterGlasses = wat
                        )
                    )
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

@Composable
private fun ProfileOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
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
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        color = NutrixColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = NutrixColors.TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = NutrixColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
