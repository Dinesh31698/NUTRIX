package com.nutrix.app

import com.nutrix.app.core.engine.DietSuggestionEngine
import com.nutrix.app.core.model.DietaryPreference
import com.nutrix.app.core.model.GoalType
import org.junit.Assert.*
import org.junit.Test

class DietSuggestionEngineTest {

    @Test
    fun testGeneratesSevenDaysOfMeals() {
        val plans = DietSuggestionEngine.generateWeekPlan(
            weekOffset = 0,
            goal = GoalType.HEALTHY_WEIGHT_GAIN,
            preference = DietaryPreference.NON_VEGETARIAN,
            allergies = emptyList()
        )

        // 7 days x 4 meals (Breakfast, Lunch, Evening Snack, Dinner) = 28 meals
        assertEquals(28, plans.size)
        val distinctDays = plans.map { it.dayOfWeek }.distinct()
        assertEquals(7, distinctDays.size)
    }

    @Test
    fun testVegetarianPreferenceExcludesMeat() {
        val vegPlans = DietSuggestionEngine.generateWeekPlan(
            weekOffset = 0,
            goal = GoalType.BALANCED_NUTRITION,
            preference = DietaryPreference.VEGETARIAN,
            allergies = emptyList()
        )

        val containsMeat = vegPlans.any {
            it.foodName.contains("Chicken", ignoreCase = true) ||
            it.foodName.contains("Mutton", ignoreCase = true) ||
            it.foodName.contains("Fish", ignoreCase = true)
        }

        assertFalse("Vegetarian plan must not contain chicken or mutton dishes", containsMeat)
    }

    @Test
    fun testNextWeekGeneratesDistinctMenu() {
        val thisWeek = DietSuggestionEngine.generateWeekPlan(
            weekOffset = 0,
            goal = GoalType.HEALTHY_WEIGHT_GAIN,
            preference = DietaryPreference.NON_VEGETARIAN,
            allergies = emptyList()
        )

        val nextWeek = DietSuggestionEngine.generateWeekPlan(
            weekOffset = 1,
            goal = GoalType.HEALTHY_WEIGHT_GAIN,
            preference = DietaryPreference.NON_VEGETARIAN,
            allergies = emptyList()
        )

        val thisWeekBreakfasts = thisWeek.filter { it.mealType == "BREAKFAST" }.map { it.foodName }
        val nextWeekBreakfasts = nextWeek.filter { it.mealType == "BREAKFAST" }.map { it.foodName }

        assertNotEquals("Next week should offer distinct schedule from this week", thisWeekBreakfasts, nextWeekBreakfasts)
    }
}
