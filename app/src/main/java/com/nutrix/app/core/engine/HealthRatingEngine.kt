package com.nutrix.app.core.engine

import com.nutrix.app.core.model.FoodCategory
import com.nutrix.app.core.model.HealthRatingResult
import com.nutrix.app.core.model.LoggedMeal
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object HealthRatingEngine {

    /**
     * Computes the daily average health rating from confirmed consumed food entries.
     *
     * Rules:
     * 1. Excludes unrated entries (ratings <= 0.0 or outside 1.0..5.0).
     * 2. Computes the arithmetic mean of valid food ratings.
     * 3. Rounds to one decimal place.
     * 4. Constrains the score within 1.0..5.0.
     * 5. Returns null if there are no valid rated entries.
     */
    fun aggregateDailyHealthRating(meals: List<LoggedMeal>): Double? {
        val validRatings = meals.map { it.rating }.filter { it in 1.0..5.0 }
        if (validRatings.isEmpty()) return null
        val mean = validRatings.average()
        val rounded = (mean * 10.0).roundToInt() / 10.0
        return rounded.coerceIn(1.0, 5.0)
    }

    /**
     * Formats a health rating score for display, returning "X.X" or "—".
     */
    fun formatRating(rating: Double?): String {
        return if (rating != null && rating in 1.0..5.0) {
            String.format(Locale.US, "%.1f", rating)
        } else {
            "—"
        }
    }

    fun calculateRating(
        category: FoodCategory,
        calories: Int,
        proteinGrams: Float,
        carbsGrams: Float,
        fatsGrams: Float,
        fiberGrams: Float,
        sugarGrams: Float,
        sodiumMg: Float,
        servingGrams: Float = 100f,
        isPackaged: Boolean = false
    ): HealthRatingResult {
        var baseScore = 3.5f
        val positives = mutableListOf<String>()
        val concerns = mutableListOf<String>()

        // 1. Fresh Fruit & Vegetable Category handling
        if (category == FoodCategory.FRUITS || category == FoodCategory.VEGETABLES) {
            baseScore += 1.0f
            positives.add("Naturally rich in micronutrients and antioxidants")
            if (fiberGrams >= 2.5f) {
                baseScore += 0.3f
                positives.add("High in gut-healthy natural dietary fiber")
            }
            val finalScore = (min(5.0f, max(1.0f, baseScore)) * 10).roundToInt() / 10.0
            val summary = when {
                finalScore >= 4.5 -> "Excellent nutrient-dense whole food"
                finalScore >= 4.0 -> "Highly nutritious fresh produce"
                else -> "Nutritious natural food"
            }
            return HealthRatingResult(finalScore, summary, positives, concerns, 1.0f)
        }

        // 2. Protein content
        val proteinRatio = if (calories > 0) (proteinGrams * 4f) / calories else 0f
        if (proteinRatio >= 0.25f || proteinGrams >= 20f) {
            baseScore += 0.5f
            positives.add("High in protein to support muscle repair and satiety")
        } else if (proteinRatio >= 0.15f || proteinGrams >= 10f) {
            baseScore += 0.2f
            positives.add("Good source of protein")
        }

        // 3. Fiber content
        if (fiberGrams >= 4f) {
            baseScore += 0.4f
            positives.add("Excellent fiber source for metabolic and gut health")
        } else if (fiberGrams >= 2f) {
            baseScore += 0.2f
            positives.add("Contains beneficial dietary fiber")
        }

        // 4. Added / Excessive Sugar
        val sugarPer100g = if (servingGrams > 0) (sugarGrams / servingGrams) * 100f else sugarGrams
        if (sugarPer100g > 20f || sugarGrams >= 25f) {
            baseScore -= 1.3f
            concerns.add("High sugar content exceeds daily recommended free sugar limits")
        } else if (sugarPer100g > 10f || sugarGrams >= 12f) {
            baseScore -= 0.5f
            concerns.add("Moderate sugar content")
        }

        // 5. Sodium / Salt
        if (sodiumMg > 700f) {
            baseScore -= 0.6f
            concerns.add("High sodium level (> 700 mg per serving)")
        } else if (sodiumMg > 400f) {
            baseScore -= 0.2f
            concerns.add("Moderate sodium content")
        }

        // 6. High Saturated / Refined Fats
        val fatRatio = if (calories > 0) (fatsGrams * 9f) / calories else 0f
        if (fatRatio > 0.55f && category != FoodCategory.SNACKS) {
            baseScore -= 0.4f
            concerns.add("High overall fat density")
        }

        // 7. Ultra-processed packaged penalty
        if (isPackaged && (sugarGrams > 15f || fatsGrams > 15f || sodiumMg > 500f)) {
            baseScore -= 0.4f
            concerns.add("Processed packaged formulation with refined ingredients")
        }

        val finalScore = (min(5.0f, max(1.0f, baseScore)) * 10).roundToInt() / 10.0

        val summary = when {
            finalScore >= 4.5 -> "Outstanding nutritional profile"
            finalScore >= 4.0 -> "Nutrient-rich, highly balanced choice"
            finalScore >= 3.3 -> "Moderately balanced, good in reasonable portions"
            finalScore >= 2.3 -> "Higher in fats/sugars; consume mindfully"
            else -> "Ultra-processed or high in empty calories"
        }

        return HealthRatingResult(
            score = finalScore,
            explanation = summary,
            positivePoints = positives,
            concernPoints = concerns,
            confidence = 1.0f
        )
    }
}