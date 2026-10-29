package com.nutrix.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.GlassCard
import com.nutrix.app.core.designsystem.NutrixButton
import com.nutrix.app.core.designsystem.NutrixColors
import com.nutrix.app.core.model.DietaryPreference
import com.nutrix.app.core.model.GoalType
import com.nutrix.app.core.model.UserProfile

@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    onComplete: (UserProfile) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(currentProfile.goalType) }
    var selectedPreference by remember { mutableStateOf(currentProfile.dietaryPreference) }
    var selectedLanguage by remember { mutableStateOf(currentProfile.language) }
    val selectedAllergies = remember { mutableStateListOf<String>() }

    val allergyOptions = listOf("Dairy", "Gluten", "Peanuts", "Tree nuts", "Soy", "Egg")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Personalize Your Plan",
                color = NutrixColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "NUTRIX tailors nutrition intelligence to your lifestyle.",
                color = NutrixColors.TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "1. What is your primary goal?",
                color = NutrixColors.ElectricBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            GoalType.values().forEach { goal ->
                val isSelected = goal == selectedGoal
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    borderColor = if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardBorder,
                    backgroundColor = if (isSelected) NutrixColors.CardSurface else NutrixColors.CardSurface.copy(alpha = 0.5f),
                    onClick = { selectedGoal = goal }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = goal.title,
                                color = if (isSelected) NutrixColors.TextPrimary else NutrixColors.TextSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = goal.subtitle,
                                color = NutrixColors.TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NutrixColors.ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "2. Dietary Preference",
                color = NutrixColors.ElectricBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DietaryPreference.values().forEach { pref ->
                    val isSelected = pref == selectedPreference
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) NutrixColors.VividViolet else NutrixColors.CardSurface)
                            .border(1.dp, if (isSelected) NutrixColors.Magenta else NutrixColors.CardBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedPreference = pref }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pref.label,
                            color = NutrixColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "3. Any Allergies to Avoid? (Optional)",
                color = NutrixColors.ElectricBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allergyOptions.take(3).forEach { allergy ->
                    val isChecked = selectedAllergies.contains(allergy)
                    AllergyChip(allergy, isChecked) {
                        if (isChecked) selectedAllergies.remove(allergy) else selectedAllergies.add(allergy)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allergyOptions.drop(3).forEach { allergy ->
                    val isChecked = selectedAllergies.contains(allergy)
                    AllergyChip(allergy, isChecked) {
                        if (isChecked) selectedAllergies.remove(allergy) else selectedAllergies.add(allergy)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "4. Preferred Language",
                color = NutrixColors.ElectricBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("en" to "English", "te" to "తెలుగు", "hi" to "हिन्दी").forEach { (code, label) ->
                    val isSelected = selectedLanguage == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NutrixColors.ElectricBlue.copy(alpha = 0.25f) else NutrixColors.CardSurface)
                            .border(1.dp, if (isSelected) NutrixColors.ElectricBlue else NutrixColors.CardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedLanguage = code }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = NutrixColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NutrixButton(
                text = "Continue to Dashboard",
                onClick = {
                    val updated = currentProfile.copy(
                        goalType = selectedGoal,
                        dietaryPreference = selectedPreference,
                        allergies = selectedAllergies.toList(),
                        language = selectedLanguage
                    )
                    onComplete(updated)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RowScope.AllergyChip(text: String, isChecked: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isChecked) NutrixColors.CoralRed.copy(alpha = 0.2f) else NutrixColors.CardSurface)
            .border(1.dp, if (isChecked) NutrixColors.CoralRed else NutrixColors.CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isChecked) NutrixColors.CoralRed else NutrixColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}