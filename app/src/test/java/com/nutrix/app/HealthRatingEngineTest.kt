package com.nutrix.app

import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.model.FoodCategory
import org.junit.Assert.*
import org.junit.Test

class HealthRatingEngineTest {

    @Test
    fun testFreshFruitReceivesHighRating() {
        val result = HealthRatingEngine.calculateRating(
            category = FoodCategory.FRUITS,
            calories = 95,
            proteinGrams = 0.5f,
            carbsGrams = 25f,
            fatsGrams = 0.3f,
            fiberGrams = 4.4f,
            sugarGrams = 19f,
            sodiumMg = 2f,
            servingGrams = 180f,
            isPackaged = false
        )

        assertTrue("Fresh fruit should score >= 4.5", result.score >= 4.5)
        assertTrue("Should highlight micronutrients or fiber", result.positivePoints.isNotEmpty())
    }

    @Test
    fun testSugaryDrinkReceivesLowRating() {
        val result = HealthRatingEngine.calculateRating(
            category = FoodCategory.PACKAGED,
            calories = 140,
            proteinGrams = 0f,
            carbsGrams = 35f,
            fatsGrams = 0f,
            fiberGrams = 0f,
            sugarGrams = 35f,
            sodiumMg = 15f,
            servingGrams = 330f,
            isPackaged = true
        )

        assertTrue("Sugary soft drink should score <= 2.0", result.score <= 2.0)
        assertTrue("Should flag excessive sugar concern", result.concernPoints.any { it.contains("sugar", ignoreCase = true) })
    }

    @Test
    fun testChickenCurryReceivesBalancedRating() {
        val result = HealthRatingEngine.calculateRating(
            category = FoodCategory.CURRIES,
            calories = 320,
            proteinGrams = 28f,
            carbsGrams = 12f,
            fatsGrams = 20f,
            fiberGrams = 2f,
            sugarGrams = 4f,
            sodiumMg = 650f,
            servingGrams = 200f,
            isPackaged = false
        )

        assertTrue("Chicken curry should score between 3.5 and 4.2", result.score in 3.5..4.2)
        assertTrue("Should praise high protein content", result.positivePoints.any { it.contains("protein", ignoreCase = true) })
    }

    @Test
    fun testUltraProcessedPackagedSnackFlagsConcerns() {
        val result = HealthRatingEngine.calculateRating(
            category = FoodCategory.PACKAGED,
            calories = 285,
            proteinGrams = 3.5f,
            carbsGrams = 30f,
            fatsGrams = 18f,
            fiberGrams = 2f,
            sugarGrams = 1f,
            sodiumMg = 170f,
            servingGrams = 52f,
            isPackaged = true
        )

        assertTrue("Packaged chips should score between 2.0 and 3.0", result.score in 2.0..3.0)
        assertTrue("Should flag processed formulation concern", result.concernPoints.isNotEmpty())
    }
}
