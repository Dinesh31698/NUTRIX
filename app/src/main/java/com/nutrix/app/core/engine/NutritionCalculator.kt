package com.nutrix.app.core.engine

import com.nutrix.app.core.model.DayNutritionSummary
import com.nutrix.app.core.model.GoalType
import com.nutrix.app.core.model.LoggedMeal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class ScaledNutrition(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float,
    val sugar: Float,
    val sodium: Float
)

object NutritionCalculator {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val dayNameFormat = SimpleDateFormat("EEE, d MMM", Locale.US)

    fun scaleNutrients(
        baseCalories: Int,
        baseProtein: Float,
        baseCarbs: Float,
        baseFats: Float,
        baseFiber: Float,
        baseSugar: Float,
        baseSodium: Float,
        baseGrams: Float,
        targetGrams: Float
    ): ScaledNutrition {
        val multiplier = if (baseGrams > 0) targetGrams / baseGrams else 1.0f
        return ScaledNutrition(
            calories = (baseCalories * multiplier).roundToInt(),
            protein = ((baseProtein * multiplier * 10).roundToInt() / 10.0f),
            carbs = ((baseCarbs * multiplier * 10).roundToInt() / 10.0f),
            fats = ((baseFats * multiplier * 10).roundToInt() / 10.0f),
            fiber = ((baseFiber * multiplier * 10).roundToInt() / 10.0f),
            sugar = ((baseSugar * multiplier * 10).roundToInt() / 10.0f),
            sodium = ((baseSodium * multiplier * 10).roundToInt() / 10.0f)
        )
    }

    fun calculateDailySummary(
        date: String,
        dayLabel: String,
        meals: List<LoggedMeal>
    ): DayNutritionSummary {
        val totalCalories = meals.sumOf { it.calories }
        val protein = meals.map { it.protein }.sum()
        val carbs = meals.map { it.carbs }.sum()
        val fats = meals.map { it.fats }.sum()
        val fiber = meals.map { it.fiber }.sum()
        val avgRating = HealthRatingEngine.aggregateDailyHealthRating(meals)

        return DayNutritionSummary(
            date = date,
            dayLabel = dayLabel,
            totalCalories = totalCalories,
            proteinGrams = (protein * 10).roundToInt() / 10.0f,
            carbsGrams = (carbs * 10).roundToInt() / 10.0f,
            fatsGrams = (fats * 10).roundToInt() / 10.0f,
            fiberGrams = (fiber * 10).roundToInt() / 10.0f,
            avgRating = avgRating,
            meals = meals
        )
    }

    fun calculateTargetCalories(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        goal: GoalType
    ): Int {
        val safeWeight = if (weightKg > 0) weightKg else 70f
        val safeHeight = if (heightCm > 0) heightCm else 175f
        val safeAge = if (age in 10..100) age else 25

        val bmr = (10f * safeWeight) + (6.25f * safeHeight) - (5f * safeAge) + 5f
        val tdee = bmr * 1.35f
        val adjusted = tdee + goal.defaultCalorieDelta

        return ((adjusted / 50f).roundToInt() * 50).coerceIn(1200, 3800)
    }

    fun getTodayIsoDate(): String {
        return isoDateFormat.format(Date())
    }

    fun getIsoDateForDayOffset(offsetFromToday: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offsetFromToday)
        return isoDateFormat.format(cal.time)
    }

    fun formatDisplayDate(isoDate: String): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: Date()
            val cal = Calendar.getInstance()
            val todayStr = isoDateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = isoDateFormat.format(cal.time)

            when (isoDate) {
                todayStr -> "Today - " + dayNameFormat.format(date)
                yesterdayStr -> "Yesterday - " + dayNameFormat.format(date)
                else -> dayNameFormat.format(date)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    fun getRecent7DaysIso(): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(isoDateFormat.format(c.time))
        }
        return list
    }
}