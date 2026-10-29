package com.nutrix.app

import com.nutrix.app.core.engine.AiAssistantEngine
import com.nutrix.app.core.model.*
import org.junit.Assert.*
import org.junit.Test

class AiAssistantEngineTest {

    @Test
    fun testChocolateHistoryQueryWithMatches() {
        val meals = listOf(
            LoggedMeal(
                foodName = "Dark Chocolate 70%",
                mealType = MealType.EVENING_SNACK,
                date = "2026-10-28",
                servingSizeGrams = 50f,
                servingDesc = "1 bar",
                calories = 290,
                protein = 4.2f,
                carbs = 22f,
                fats = 21f
            )
        )

        val response = AiAssistantEngine.generateResponse(
            query = "How often did I eat chocolate this week?",
            recentMeals = meals,
            dietPlans = emptyList(),
            userProfile = UserProfile()
        )

        assertTrue("Should detect 1 chocolate entry", response.contains("1 time(s)"))
        assertTrue("Should state calories", response.contains("290 kcal"))
    }

    @Test
    fun testHighProteinBreakfastQuery() {
        val response = AiAssistantEngine.generateResponse(
            query = "Suggest a higher-protein breakfast",
            recentMeals = emptyList(),
            dietPlans = emptyList(),
            userProfile = UserProfile()
        )

        assertTrue("Should recommend eggs or paneer", response.contains("Eggs") || response.contains("Paneer"))
    }
}
