package com.nutrix.app.core.engine

import com.nutrix.app.core.database.DietPlanEntity
import com.nutrix.app.core.model.DietaryPreference
import com.nutrix.app.core.model.GoalType
import com.nutrix.app.core.model.MealType

object DietSuggestionEngine {

    fun generateWeekPlan(
        weekOffset: Int,
        goal: GoalType,
        preference: DietaryPreference,
        allergies: List<String>
    ): List<DietPlanEntity> {
        val plans = mutableListOf<DietPlanEntity>()
        val isVeg = preference == DietaryPreference.VEGETARIAN || preference == DietaryPreference.VEGAN
        val isVegan = preference == DietaryPreference.VEGAN

        val paneerAlt = if (isVegan) "Tofu (Soya Paneer)" else "Fresh Paneer"

        for (day in 1..7) {
            if (weekOffset == 0) {
                // THIS WEEK (Days 1 to 7 - Each day has authentic distinct meals)
                when (day) {
                    1 -> { // Monday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = "Oats Porridge with Milk, Banana & Almonds",
                                portionDesc = "1 bowl (250 g)", calories = 420, protein = 15f, carbs = 65f, fats = 12f,
                                isCompleted = true, reason = "Complex oats beta-glucan & healthy fats for sustained morning focus"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Paneer Butter Masala with 2 Whole Wheat Chapatis" else "Chicken Curry with Steamed Basmati Rice",
                                portionDesc = "1 cup curry + staples (350 g)", calories = 580, protein = if (isVeg) 22f else 34f, carbs = 66f, fats = 20f,
                                isCompleted = true, reason = "Balanced protein-carb ratio tailored to daily energy demand"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Roasted Makhana & Fresh Tender Coconut Water",
                                portionDesc = "1 bowl makhana + 250ml water", calories = 160, protein = 5f, carbs = 32f, fats = 1f,
                                isCompleted = false, reason = "Electrolyte replenishment with crunchy low-fat minerals"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "Palak Paneer with 2 Phulkas & Salad" else "Tandoori Chicken (2 pcs) with Phulka & Cucumber Salad",
                                portionDesc = "1 plate (300 g)", calories = 460, protein = if (isVeg) 20f else 38f, carbs = 42f, fats = 16f,
                                isCompleted = false, reason = "High micronutrients (iron & folate) with lean evening protein"
                            )
                        )
                    }
                    2 -> { // Tuesday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = if (isVeg) "Idli with Sambar & Coconut Chutney (3 idlis)" else "Idli with Sambar & 2 Boiled Eggs",
                                portionDesc = "3 idlis + sambar bowl", calories = 380, protein = if (isVeg) 11f else 22f, carbs = 58f, fats = 8f,
                                isCompleted = false, reason = "Steamed fermented rice & lentils promote gut microbiome diversity"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = "Dal Tadka with Steamed Brown Rice & Curd",
                                portionDesc = "1 cup dal + 1 cup rice + curd", calories = 510, protein = 18f, carbs = 76f, fats = 11f,
                                isCompleted = false, reason = "High soluble plant fiber and cooling live probiotics"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Sprouted Moong Salad with Lemon & Green Chilies",
                                portionDesc = "1 cup (150 g)", calories = 145, protein = 11f, carbs = 24f, fats = 1f,
                                isCompleted = false, reason = "Living sprouted enzymes and bioavailable plant micronutrients"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "Paneer Bhurji with 2 Multigrain Chapatis" else "Chicken Chettinad with 2 Whole Wheat Chapatis",
                                portionDesc = "1 cup curry + 2 chapatis", calories = 480, protein = if (isVeg) 24f else 32f, carbs = 46f, fats = 18f,
                                isCompleted = false, reason = "Antioxidant black pepper stimulates metabolic digestive rate"
                            )
                        )
                    }
                    3 -> { // Wednesday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = "Vegetable Poha with Roasted Peanuts & Lime",
                                portionDesc = "1 plate (200 g)", calories = 340, protein = 9f, carbs = 52f, fats = 10f,
                                isCompleted = false, reason = "Iron-rich flattened rice paired with Vitamin C from fresh lemon"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Rajma Masala with Steamed Basmati Rice & Salad" else "Chicken Tikka Masala with Jeera Rice",
                                portionDesc = "1 plate (350 g)", calories = 560, protein = if (isVeg) 19f else 32f, carbs = 74f, fats = 15f,
                                isCompleted = false, reason = "High soluble fiber from kidney beans stabilizes post-prandial glucose"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Handful of Roasted Almonds & Green Tea",
                                portionDesc = "25 almonds (30 g)", calories = 175, protein = 6f, carbs = 6f, fats = 15f,
                                isCompleted = false, reason = "Vitamin E antioxidant protecting cellular lipid membranes"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "Mixed Vegetable Kurma with 2 Phulkas" else "Egg Curry (2 eggs) with 2 Whole Wheat Chapatis",
                                portionDesc = "1 cup curry + 2 rotis", calories = 450, protein = if (isVeg) 12f else 24f, carbs = 48f, fats = 16f,
                                isCompleted = false, reason = "Choline and lutein support restorative sleep architecture"
                            )
                        )
                    }
                    4 -> { // Thursday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = "Besan Chilla with Grated Veggies & Mint Chutney",
                                portionDesc = "2 chillas (160 g)", calories = 310, protein = 15f, carbs = 36f, fats = 9f,
                                isCompleted = false, reason = "Gluten-free chickpea batter provides high fiber and sustained satiety"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Chana Masala (Chole) with 2 Whole Wheat Kulchas" else "Chicken Biryani with Cucumber Raita",
                                portionDesc = "1 plate (350 g)", calories = 580, protein = if (isVeg) 18f else 34f, carbs = 68f, fats = 18f,
                                isCompleted = false, reason = "Complete meal with whole spices, high protein, and probiotic raita"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Fresh Guava Slices with Chaat Masala",
                                portionDesc = "1 medium guava (120 g)", calories = 70, protein = 3f, carbs = 15f, fats = 1f,
                                isCompleted = false, reason = "6 grams of dietary fiber and 4x Vitamin C of citrus fruit"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "Baingan Bharta with Yellow Dal & 2 Phulkas" else "Goan Fish Curry with Steamed Rice",
                                portionDesc = "1 bowl curry + staples", calories = 470, protein = if (isVeg) 16f else 30f, carbs = 52f, fats = 15f,
                                isCompleted = false, reason = "Lean marine Omega-3 fatty acids or rich eggplant nasunin"
                            )
                        )
                    }
                    5 -> { // Friday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = if (isVeg) "Methi Thepla with Low-fat Curd (2 theplas)" else "Scrambled Eggs (3 eggs) with Whole Wheat Toast",
                                portionDesc = "1 plate (180 g)", calories = 340, protein = if (isVeg) 10f else 22f, carbs = 38f, fats = 12f,
                                isCompleted = false, reason = "Fenugreek seeds and bioavailable protein improve insulin sensitivity"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Dal Makhani with Jeera Brown Rice & Salad" else "Kadai Chicken with 2 Chapatis & Salad",
                                portionDesc = "1 cup curry + rice/roti", calories = 550, protein = if (isVeg) 17f else 32f, carbs = 65f, fats = 19f,
                                isCompleted = false, reason = "Capsicum Vitamin C boosts non-heme iron absorption"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Roasted Chana & Fresh Masala Chai (Low Sugar)",
                                portionDesc = "1 cup chana + 150ml tea", calories = 180, protein = 8f, carbs = 26f, fats = 3f,
                                isCompleted = false, reason = "Traditional tea-time fuel rich in polyphenol flavonoids"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "$paneerAlt Tikka with Grilled Bell Peppers & 2 Phulkas" else "Grilled Chicken Breast with Sautéed Veggies & Roti",
                                portionDesc = "1 plate (300 g)", calories = 480, protein = if (isVeg) 22f else 42f, carbs = 38f, fats = 14f,
                                isCompleted = false, reason = "Maximum lean protein density to repair muscular micro-trauma"
                            )
                        )
                    }
                    6 -> { // Saturday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = "Crispy Plain Dosa with Sambar & Mint Chutney",
                                portionDesc = "1 medium dosa + sambar", calories = 320, protein = 8f, carbs = 52f, fats = 7f,
                                isCompleted = false, reason = "Weekend clean breakfast with fermented bio-digestibility"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Soya Chunks Curry with Steamed Basmati Rice & Salad" else "Mutton Keema Matar with 2 Whole Wheat Chapatis",
                                portionDesc = "1 plate (350 g)", calories = 570, protein = if (isVeg) 28f else 32f, carbs = 64f, fats = 18f,
                                isCompleted = false, reason = "High protein legume or lean minced meat with green pea fiber"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Greek Yogurt with Chopped Fresh Strawberries",
                                portionDesc = "1 cup (150 g)", calories = 120, protein = 11f, carbs = 12f, fats = 1f,
                                isCompleted = false, reason = "Prebiotic berry fiber combined with live probiotic cultures"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = if (isVeg) "Aloo Gobi with Dal Tadka & 2 Phulkas" else "Kerala Fish Curry with Steamed Rice & Greens",
                                portionDesc = "1 plate (320 g)", calories = 460, protein = if (isVeg) 15f else 28f, carbs = 58f, fats = 14f,
                                isCompleted = false, reason = "Potassium rich vegetables promoting vascular decompression"
                            )
                        )
                    }
                    7 -> { // Sunday
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                                foodName = if (isVeg) "Paneer Paratha with Plain Curd (1 paratha)" else "Aloo Paratha with 2 Boiled Eggs & Curd",
                                portionDesc = "1 paratha + curd (200 g)", calories = 410, protein = if (isVeg) 16f else 20f, carbs = 46f, fats = 16f,
                                isCompleted = false, reason = "Wholesome Sunday hearty breakfast with quality dairy fats"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.LUNCH.name,
                                foodName = if (isVeg) "Vegetable Biryani with Cucumber Onion Raita" else "Chicken Dum Biryani with Mixed Raita",
                                portionDesc = "1 large plate (380 g)", calories = 590, protein = if (isVeg) 14f else 34f, carbs = 72f, fats = 20f,
                                isCompleted = false, reason = "Sunday special celebration meal rich in whole aromatic spices"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                                foodName = "Roasted Walnuts & 1 Apple",
                                portionDesc = "1 apple + 5 walnut halves", calories = 190, protein = 3f, carbs = 28f, fats = 9f,
                                isCompleted = false, reason = "Neuro-protective Alpha-Linolenic Acid paired with apple pectin"
                            )
                        )
                        plans.add(
                            DietPlanEntity(
                                weekOffset = 0, dayOfWeek = day, mealType = MealType.DINNER.name,
                                foodName = "Light Moong Dal Khichdi with Ghee & Tomato Salad",
                                portionDesc = "1 bowl (250 g)", calories = 380, protein = 14f, carbs = 56f, fats = 9f,
                                isCompleted = false, reason = "Detoxifying light khichdi preps digestion for the upcoming work week"
                            )
                        )
                    }
                }
            } else {
                // NEXT WEEK (Forward Looking Optimized Nutrition Cycle)
                val breakfastFood = when (day % 3) {
                    0 -> if (isVeg) "Masala Dosa with Sambar & Chutney" else "Boiled Eggs (3) with Multigrain Toast"
                    1 -> "Whey Protein Shake with Oats & Banana"
                    else -> "Vegetable Upma with Roasted Cashews"
                }
                val lunchFood = when (day % 3) {
                    0 -> if (isVeg) "Kadhi Pakora with Steamed Brown Rice" else "Chicken Korma with Whole Wheat Chapati"
                    1 -> if (isVeg) "Paneer Tikka Masala with Jeera Rice" else "Goan Fish Curry with Steamed Rice & Salad"
                    else -> if (isVeg) "Rajma Chawal with Kachumber Salad" else "Chicken Curry with Rice & Spiced Dal"
                }
                val snackFood = when (day % 2) {
                    0 -> "Fresh Tender Coconut Water & Almonds"
                    else -> "Roasted Makhana & Fresh Fruit Bowl"
                }
                val dinnerFood = when (day % 3) {
                    0 -> if (isVeg) "Palak Paneer with 2 Phulkas" else "Grilled Chicken Breast with Steamed Broccoli"
                    1 -> if (isVeg) "Dal Makhani with 2 Chapatis" else "Egg Curry (2 eggs) with 2 Phulkas & Salad"
                    else -> if (isVeg) "Mixed Vegetable Kurma with Phulka" else "Fish Curry with Brown Rice"
                }

                plans.add(
                    DietPlanEntity(
                        weekOffset = 1, dayOfWeek = day, mealType = MealType.BREAKFAST.name,
                        foodName = breakfastFood, portionDesc = "1 standard serving (220 g)",
                        calories = 380, protein = 18f, carbs = 52f, fats = 10f,
                        isCompleted = false, reason = "Next week morning metabolic kickstarter"
                    )
                )
                plans.add(
                    DietPlanEntity(
                        weekOffset = 1, dayOfWeek = day, mealType = MealType.LUNCH.name,
                        foodName = lunchFood, portionDesc = "1 wholesome plate (350 g)",
                        calories = 570, protein = 30f, carbs = 65f, fats = 18f,
                        isCompleted = false, reason = "Goal-aligned macronutrient distribution for peak daytime focus"
                    )
                )
                plans.add(
                    DietPlanEntity(
                        weekOffset = 1, dayOfWeek = day, mealType = MealType.EVENING_SNACK.name,
                        foodName = snackFood, portionDesc = "1 portion (150 g)",
                        calories = 160, protein = 5f, carbs = 24f, fats = 6f,
                        isCompleted = false, reason = "Natural minerals to keep blood sugar stable before dinner"
                    )
                )
                plans.add(
                    DietPlanEntity(
                        weekOffset = 1, dayOfWeek = day, mealType = MealType.DINNER.name,
                        foodName = dinnerFood, portionDesc = "1 light plate (300 g)",
                        calories = 460, protein = 28f, carbs = 44f, fats = 14f,
                        isCompleted = false, reason = "Clean evening digestion supporting deep restorative sleep"
                    )
                )
            }
        }
        return plans
    }
}
