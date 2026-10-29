package com.nutrix.app

import com.nutrix.app.core.designsystem.FoodIconHelper
import com.nutrix.app.core.engine.AiAssistantEngine
import com.nutrix.app.core.engine.DietSuggestionEngine
import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.*
import org.junit.Assert.*
import org.junit.Test

class NutritionCalculatorTest {

    @Test
    fun testScaleNutrientsCalculatesCorrectly() {
        val scaled = NutritionCalculator.scaleNutrients(
            baseCalories = 200,
            baseProtein = 10f,
            baseCarbs = 20f,
            baseFats = 8f,
            baseFiber = 2f,
            baseSugar = 4f,
            baseSodium = 100f,
            baseGrams = 100f,
            targetGrams = 200f
        )

        assertEquals(400, scaled.calories)
        assertEquals(20f, scaled.protein, 0.01f)
        assertEquals(40f, scaled.carbs, 0.01f)
        assertEquals(16f, scaled.fats, 0.01f)
    }

    @Test
    fun testDailySummaryCalculation() {
        val meals = listOf(
            LoggedMeal(
                foodName = "Chicken Curry",
                mealType = MealType.LUNCH,
                date = "2026-10-29",
                servingSizeGrams = 200f,
                servingDesc = "1 cup",
                calories = 320,
                protein = 28f,
                carbs = 12f,
                fats = 20f,
                rating = 4.0
            ),
            LoggedMeal(
                foodName = "Rice",
                mealType = MealType.LUNCH,
                date = "2026-10-29",
                servingSizeGrams = 150f,
                servingDesc = "1 cup",
                calories = 195,
                protein = 4.2f,
                carbs = 44f,
                fats = 0.5f,
                rating = 4.2
            )
        )

        val summary = NutritionCalculator.calculateDailySummary(
            date = "2026-10-29",
            dayLabel = "Today",
            meals = meals
        )

        assertEquals(515, summary.totalCalories)
        assertEquals(32.2f, summary.proteinGrams, 0.1f)
        assertEquals(56f, summary.carbsGrams, 0.1f)
        assertEquals(2, summary.meals.size)
        assertEquals(4.1, summary.avgRating!!, 0.05)
    }

    @Test
    fun testTargetCaloriesAdjustment() {
        val weightLossTarget = NutritionCalculator.calculateTargetCalories(
            weightKg = 70f,
            heightCm = 175f,
            age = 25,
            goal = GoalType.WEIGHT_LOSS
        )
        val muscleGainTarget = NutritionCalculator.calculateTargetCalories(
            weightKg = 70f,
            heightCm = 175f,
            age = 25,
            goal = GoalType.MUSCLE_GAIN
        )

        assertTrue("Muscle gain target should be higher than weight loss", muscleGainTarget > weightLossTarget)
    }

    @Test
    fun testDietSuggestionEngineGeneratesFullWeek() {
        val week0 = DietSuggestionEngine.generateWeekPlan(
            weekOffset = 0,
            goal = GoalType.HEALTHY_WEIGHT_GAIN,
            preference = DietaryPreference.NON_VEGETARIAN,
            allergies = emptyList()
        )

        // 7 days * 4 meals per day = 28 planned meals
        assertEquals(28, week0.size)

        // Ensure days 1 to 7 are present
        val daysPresent = week0.map { it.dayOfWeek }.toSet()
        assertEquals(7, daysPresent.size)

        // Ensure meals are unique between Monday and Tuesday
        val mondayBreakfast = week0.first { it.dayOfWeek == 1 && it.mealType == MealType.BREAKFAST.name }.foodName
        val tuesdayBreakfast = week0.first { it.dayOfWeek == 2 && it.mealType == MealType.BREAKFAST.name }.foodName
        assertNotEquals("Days should have varied meals", mondayBreakfast, tuesdayBreakfast)
    }

    @Test
    fun testAiAssistantEngineAnswersProteinQuery() {
        val user = UserProfile(name = "Alex", dailyCalorieTarget = 2000)
        val todayIso = NutritionCalculator.getTodayIsoDate()
        val meals = listOf(
            LoggedMeal(
                foodName = "Grilled Chicken Breast",
                mealType = MealType.LUNCH,
                date = todayIso,
                servingSizeGrams = 150f,
                servingDesc = "1 fillet",
                calories = 240,
                protein = 46f,
                carbs = 0f,
                fats = 5f
            )
        )

        val reply = AiAssistantEngine.generateResponse(
            query = "How much protein did I have today?",
            recentMeals = meals,
            dietPlans = emptyList(),
            userProfile = user
        )

        assertTrue(reply.contains("46.0g of protein"))
    }

    @Test
    fun testFoodIconHelperDoesNotAssignPotatoToBeverages() {
        val cokeEmoji = FoodIconHelper.getFoodEmoji("Coca-Cola Can", FoodCategory.BEVERAGES)
        val chipsEmoji = FoodIconHelper.getFoodEmoji("Lay's Classic Potato Chips", FoodCategory.PACKAGED)
        val appleEmoji = FoodIconHelper.getFoodEmoji("Fresh Whole Apple", FoodCategory.FRUITS)
        val biryaniEmoji = FoodIconHelper.getFoodEmoji("Chicken Biryani", FoodCategory.RICE_ITEMS)

        assertEquals("🥤", cokeEmoji)
        assertEquals("🍟", chipsEmoji)
        assertEquals("🍎", appleEmoji)
        assertEquals("🍛", biryaniEmoji)
    }

    @Test
    fun testHealthRatingEngineScoresProperly() {
        val fruitRating = HealthRatingEngine.calculateRating(
            category = FoodCategory.FRUITS,
            calories = 95,
            proteinGrams = 0.5f,
            carbsGrams = 25f,
            fatsGrams = 0.3f,
            fiberGrams = 4.4f,
            sugarGrams = 19f,
            sodiumMg = 2f,
            isPackaged = false
        )

        val sodaRating = HealthRatingEngine.calculateRating(
            category = FoodCategory.BEVERAGES,
            calories = 140,
            proteinGrams = 0f,
            carbsGrams = 35f,
            fatsGrams = 0f,
            fiberGrams = 0f,
            sugarGrams = 35f,
            sodiumMg = 15f,
            isPackaged = true
        )

        assertTrue("Fruit rating should be higher than sugary soda", fruitRating.score > sodaRating.score)
    }

    @Test
    fun testHealthRatingAggregation_EmptyMeals_ReturnsNullAndDash() {
        val emptyMeals = emptyList<LoggedMeal>()
        val avg = HealthRatingEngine.aggregateDailyHealthRating(emptyMeals)
        assertNull("Empty meals list must produce null health rating", avg)
        assertEquals("—", HealthRatingEngine.formatRating(avg))

        val summary = NutritionCalculator.calculateDailySummary("2026-10-29", "Today", emptyMeals)
        assertNull(summary.avgRating)
    }

    @Test
    fun testHealthRatingAggregation_SingleFood() {
        val meals = listOf(
            LoggedMeal(
                foodName = "Fresh Apple",
                mealType = MealType.EVENING_SNACK,
                date = "2026-10-29",
                servingSizeGrams = 100f,
                servingDesc = "1 medium",
                calories = 95,
                protein = 0.5f,
                carbs = 25f,
                fats = 0.3f,
                rating = 4.8
            )
        )
        val avg = HealthRatingEngine.aggregateDailyHealthRating(meals)
        assertNotNull(avg)
        assertEquals(4.8, avg!!, 0.01)
        assertEquals("4.8", HealthRatingEngine.formatRating(avg))
    }

    @Test
    fun testHealthRatingAggregation_MultipleFoodsWithDynamicAddAndDelete() {
        val apple = LoggedMeal(
            foodName = "Fresh Apple",
            mealType = MealType.BREAKFAST,
            date = "2026-10-29",
            servingSizeGrams = 100f,
            servingDesc = "1 fruit",
            calories = 95,
            protein = 0.5f,
            carbs = 25f,
            fats = 0.3f,
            rating = 4.8
        )
        val chickenCurry = LoggedMeal(
            foodName = "Chicken Curry",
            mealType = MealType.LUNCH,
            date = "2026-10-29",
            servingSizeGrams = 200f,
            servingDesc = "1 cup",
            calories = 320,
            protein = 28f,
            carbs = 12f,
            fats = 20f,
            rating = 3.8
        )
        val chocolate = LoggedMeal(
            foodName = "Milk Chocolate Bar",
            mealType = MealType.EVENING_SNACK,
            date = "2026-10-29",
            servingSizeGrams = 40f,
            servingDesc = "1 bar",
            calories = 220,
            protein = 3f,
            carbs = 24f,
            fats = 12f,
            rating = 2.4
        )

        // 3 foods logged: (4.8 + 3.8 + 2.4) / 3 = 11.0 / 3 = 3.666... -> rounds to 3.7
        val meals = mutableListOf(apple, chickenCurry, chocolate)
        var avg = HealthRatingEngine.aggregateDailyHealthRating(meals)
        assertNotNull(avg)
        assertEquals(3.7, avg!!, 0.01)
        assertEquals("3.7", HealthRatingEngine.formatRating(avg))

        // When chocolate is deleted: (4.8 + 3.8) / 2 = 8.6 / 2 = 4.3 -> rounds to 4.3
        meals.remove(chocolate)
        avg = HealthRatingEngine.aggregateDailyHealthRating(meals)
        assertNotNull(avg)
        assertEquals(4.3, avg!!, 0.01)
        assertEquals("4.3", HealthRatingEngine.formatRating(avg))
    }

    @Test
    fun testHealthRatingAggregation_IgnoresUnratedOrInvalidEntries() {
        val ratedMeal = LoggedMeal(
            foodName = "Greek Yogurt",
            mealType = MealType.BREAKFAST,
            date = "2026-10-29",
            servingSizeGrams = 150f,
            servingDesc = "1 cup",
            calories = 130,
            protein = 15f,
            carbs = 6f,
            fats = 4f,
            rating = 4.6
        )
        val unratedMealZero = LoggedMeal(
            foodName = "Unknown Seasoning",
            mealType = MealType.LUNCH,
            date = "2026-10-29",
            servingSizeGrams = 5f,
            servingDesc = "1 pinch",
            calories = 5,
            protein = 0f,
            carbs = 1f,
            fats = 0f,
            rating = 0.0 // unrated
        )
        val unratedMealNegative = LoggedMeal(
            foodName = "Water",
            mealType = MealType.EVENING_SNACK,
            date = "2026-10-29",
            servingSizeGrams = 250f,
            servingDesc = "1 glass",
            calories = 0,
            protein = 0f,
            carbs = 0f,
            fats = 0f,
            rating = -1.0 // invalid
        )

        val meals = listOf(ratedMeal, unratedMealZero, unratedMealNegative)
        val avg = HealthRatingEngine.aggregateDailyHealthRating(meals)
        assertNotNull(avg)
        assertEquals("Unrated items must be ignored and not dilute the rating to zero", 4.6, avg!!, 0.01)
    }
}
