package com.nutrix.app.core.engine

import com.nutrix.app.core.model.DietPlan
import com.nutrix.app.core.model.LoggedMeal
import com.nutrix.app.core.model.UserProfile
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

object AiAssistantEngine {

    fun generateResponse(
        query: String,
        recentMeals: List<LoggedMeal>,
        dietPlans: List<DietPlan>,
        userProfile: UserProfile
    ): String {
        val q = query.trim().lowercase(Locale.ROOT)
        val todayIso = NutritionCalculator.getTodayIsoDate()
        val yesterdayIso = NutritionCalculator.getIsoDateForDayOffset(-1)

        val todayMeals = recentMeals.filter { it.date == todayIso }
        val yesterdayMeals = recentMeals.filter { it.date == yesterdayIso }

        val todayCalories = todayMeals.sumOf { it.calories }
        val todayProtein = todayMeals.map { it.protein }.sum()
        val remainingCalories = (userProfile.dailyCalorieTarget - todayCalories).coerceAtLeast(0)

        return when {
            // Greetings / Multilingual
            q.contains("hello") || q.contains("hi ") || q == "hi" || q.contains("hey") || q.contains("namaste") || q.contains("నమస్కారం") || q.contains("नमस्ते") -> {
                "Hello ${userProfile.name}! 👋 I am your NUTRIX On-Device Intelligence Assistant. I have full local access to your meals, daily calorie target (${userProfile.dailyCalorieTarget} kcal), and diet plan. How can I help your nutrition today?"
            }

            // Protein queries
            q.contains("protein") && (q.contains("today") || q.contains("how much") || q.contains("consumed") || q.contains("ate") || q.contains("have")) -> {
                val targetProtein = (userProfile.dailyCalorieTarget * 0.25f / 4f).toInt()
                val pct = if (targetProtein > 0) ((todayProtein / targetProtein) * 100).toInt() else 0
                "Today you have consumed ${String.format(Locale.US, "%.1f", todayProtein)}g of protein across ${todayMeals.size} meal(s).\nYour recommended target for ${userProfile.goalType.title} is ~${targetProtein}g ($pct% achieved).\nTop protein contributors today: " +
                        if (todayMeals.isNotEmpty()) todayMeals.sortedByDescending { it.protein }.take(2).joinToString { "${it.foodName} (${it.protein}g)" }
                        else "No meals logged yet today."
            }

            // Yesterday's meals query
            q.contains("yesterday") -> {
                if (yesterdayMeals.isNotEmpty()) {
                    val yCals = yesterdayMeals.sumOf { it.calories }
                    val yProt = yesterdayMeals.map { it.protein }.sum()
                    val mealList = yesterdayMeals.joinToString("\n") { "• ${it.foodName} (${it.servingDesc}) — ${it.calories} kcal, ${it.protein}g protein" }
                    "Yesterday (${yesterdayIso}) you logged ${yesterdayMeals.size} meal(s) totaling $yCals kcal and ${String.format(Locale.US, "%.1f", yProt)}g protein:\n$mealList"
                } else {
                    "No meals were recorded for yesterday ($yesterdayIso). Make sure to log all meals to maintain consistent tracking!"
                }
            }

            // Calories consumed or remaining today
            q.contains("calorie") || q.contains("calories") || q.contains("calories left") || q.contains("remaining") || q.contains("how much can i eat") -> {
                "Today's Calorie Overview:\n• Consumed: $todayCalories kcal\n• Daily Target: ${userProfile.dailyCalorieTarget} kcal\n• Remaining Budget: $remainingCalories kcal\n" +
                        if (todayCalories > userProfile.dailyCalorieTarget) "You have exceeded your target by ${todayCalories - userProfile.dailyCalorieTarget} kcal. Try opting for light fiber/salads for dinner."
                        else "You have ample room for a nutrient-dense, high-protein meal to reach your target!"
            }

            // Food comparisons: Roti vs Rice
            q.contains("roti") && q.contains("rice") -> {
                "Chapati (Roti) vs Steamed Rice Comparison (per 100g):\n• Chapati: ~270 kcal, 9g protein, 55g carbs, 7g fiber (Atta bran)\n• White Rice: ~130 kcal (cooked), 2.7g protein, 28g carbs, 0.4g fiber\n💡 Verdict: Whole wheat chapatis provide significantly higher dietary fiber and protein which lowers insulin spikes. However, white rice is easier to digest post-workout. Combining either with Dal or Chicken provides a balanced amino acid profile."
            }

            // Food comparisons: Paneer vs Chicken / Eggs
            (q.contains("paneer") && (q.contains("chicken") || q.contains("egg"))) || (q.contains("chicken") && q.contains("egg")) -> {
                "Protein Comparison (per 100g):\n• Grilled Chicken Breast: 31g protein, 165 kcal, 3.6g fat (Pure complete lean protein)\n• Boiled Whole Eggs: 13g protein, 143 kcal, 9.5g fat (Gold standard bioavailability)\n• Fresh Paneer: 18g protein, 265 kcal, 20g fat (Rich in calcium & slow-digesting casein)\n💡 Verdict: Chicken is best for lean fat loss. Paneer is fantastic for vegetarians and evening satiety due to slow casein absorption. Whole eggs provide essential choline and lutein."
            }

            // Food comparisons: Apple vs Banana
            q.contains("apple") && q.contains("banana") -> {
                "Apple vs Banana Comparison:\n• 1 Medium Apple (180g): ~95 kcal, 4.4g pectin fiber, 19g sugar (low glycemic index)\n• 1 Medium Banana (120g): ~105 kcal, 3.1g fiber, 420mg potassium, 27g carbs\n💡 Verdict: Bananas are ideal 30-45 minutes before intense workouts for rapid glycogen replenishment. Apples are superior for appetite control between meals due to soluble pectin fiber."
            }

            // Chocolate query
            q.contains("chocolate") && (q.contains("how often") || q.contains("how many times") || q.contains("eat")) -> {
                val matches = recentMeals.filter { it.foodName.lowercase().contains("chocolate") }
                if (matches.isNotEmpty()) {
                    val count = matches.size
                    val totalCalories = matches.sumOf { it.calories }
                    "You logged chocolate $count time(s) in your history, totaling $totalCalories kcal. Consider choosing 70%+ dark chocolate for beneficial polyphenols and lower added sugar."
                } else {
                    "You haven't logged any chocolate recently. Great job maintaining your snack boundaries!"
                }
            }

            // High-protein breakfast recommendation
            q.contains("protein breakfast") || q.contains("high protein breakfast") || q.contains("breakfast recommendation") -> {
                "Top High-Protein Indian Breakfast Options:\n1. 3 Egg White + 1 Whole Egg Omelette with 1 Whole Wheat Toast (~20g protein, 220 kcal)\n2. Paneer Bhurji (150g) with 1 Phulka (~22g protein, 330 kcal)\n3. Besan Chilla with Sprouted Moong Filling (~18g protein, 290 kcal)\n4. Rolled Oats with 1 Scoop Whey Protein & Chia Seeds (~30g protein, 320 kcal)\nThese prevent mid-morning glucose crashes and sustain satiety until lunch."
            }

            // Tomorrow's plan
            q.contains("plan") && (q.contains("tomorrow") || q.contains("next day")) -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, 1)
                val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 1
                    Calendar.TUESDAY -> 2
                    Calendar.WEDNESDAY -> 3
                    Calendar.THURSDAY -> 4
                    Calendar.FRIDAY -> 5
                    Calendar.SATURDAY -> 6
                    Calendar.SUNDAY -> 7
                    else -> 1
                }
                val tomorrowPlans = dietPlans.filter { it.dayOfWeek == dayOfWeek && it.weekOffset == 0 }
                if (tomorrowPlans.isNotEmpty()) {
                    val mealsText = tomorrowPlans.joinToString("\n") { "• ${it.mealType.displayName}: ${it.foodName} (${it.calories} kcal)" }
                    val total = tomorrowPlans.sumOf { it.calories }
                    "Here is your scheduled plan for tomorrow:\n$mealsText\nTotal planned: $total kcal."
                } else {
                    "No specific meal plan found for tomorrow. Head to the Diet Plan tab to generate or review your weekly schedule!"
                }
            }

            // Dinner suggestion
            q.contains("dinner") || q.contains("what should i eat tonight") -> {
                "Recommended Dinner options for ${userProfile.goalType.title}:\n• Vegetarian: Palak Paneer with 2 Phulkas and a large Cucumber Tomato Salad (~460 kcal, 20g protein)\n• Non-Vegetarian: Grilled Tandoori Chicken Breast with Sautéed Spinach and 1 Chapati (~420 kcal, 38g protein)\n• Light: Moong Dal Khichdi with a bowl of Curd (~380 kcal, 16g protein)"
            }

            // Curries advice
            q.contains("curry") || q.contains("curries") -> {
                "Indian curries can be exceptionally healthy when cooked with whole spices (turmeric, black pepper, cumin) and moderate oils. For weight loss, choose tomato-and-onion gravies over heavy cream or cashew gravies. For muscle gain, Chicken Curry, Fish Curry, or Paneer Bhurji provide excellent protein!"
            }

            // Healthy snacks
            q.contains("snack") || q.contains("healthy snack") -> {
                "Top nutrient-dense Indian snacks:\n• Roasted Makhana (Fox Nuts) with black salt (~110 kcal, high minerals)\n• Roasted Chana (Bhuna Chana) with a cup of Green Tea (~150 kcal, 7.5g protein)\n• Sprouted Moong Salad with lemon juice (~145 kcal, 11g protein)\n• Handful of Raw Almonds & Walnuts (~180 kcal, healthy Omega-3s)"
            }

            // Water / Hydration
            q.contains("water") || q.contains("hydration") -> {
                "Hydration Tip: Your daily goal is ${userProfile.dailyWaterGlasses} glasses (~2 liters). Adequate water intake boosts resting metabolic rate by up to 30% for 40 minutes and aids kidney processing of dietary protein."
            }

            // Goal specific fallback
            else -> {
                "Based on your profile for ${userProfile.goalType.title} (${userProfile.dailyCalorieTarget} kcal/day):\n• Today's Meals Logged: ${todayMeals.size} ($todayCalories kcal, ${todayProtein.toInt()}g protein)\n• Remaining Calorie Budget: $remainingCalories kcal\n• Daily Water: ${userProfile.dailyWaterGlasses} glasses\nAsk me about comparing foods (e.g. 'Roti vs Rice', 'Paneer vs Chicken'), tomorrow's plan, or meal recommendations!"
            }
        }
    }
}
