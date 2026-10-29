package com.nutrix.app.core.model

enum class FoodCategory(val displayName: String) {
    ALL("All"),
    CURRIES("Curries"),
    RICE_ITEMS("Rice Items"),
    GRAINS("Grains & Breads"),
    SNACKS("Snacks"),
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    DAIRY("Dairy"),
    PROTEIN("Protein Foods"),
    BEVERAGES("Beverages"),
    PACKAGED("Packaged")
}

enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    EVENING_SNACK("Evening Snack"),
    DINNER("Dinner")
}

enum class GoalType(val title: String, val subtitle: String, val defaultCalorieDelta: Int) {
    HEALTHY_WEIGHT_GAIN("Healthy Weight Gain", "Gain weight with nutritious and balanced meals", 350),
    WEIGHT_LOSS("Weight Loss", "Sustainable fat loss with nutrient-dense foods", -400),
    MUSCLE_GAIN("Muscle Gain", "High protein distribution for muscle hypertrophy", 250),
    WEIGHT_MAINTENANCE("Weight Maintenance", "Maintain energy balance and consistent habits", 0),
    BALANCED_NUTRITION("Balanced Nutrition", "Diverse micronutrients and gut-healthy fiber", 0)
}

enum class DietaryPreference(val label: String) {
    VEGETARIAN("Vegetarian"),
    NON_VEGETARIAN("Non-Veg"),
    VEGAN("Vegan"),
    EGGETARIAN("Eggetarian")
}

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val category: FoodCategory,
    val servingSizeGrams: Float = 100f,
    val servingUnit: String = "100 g",
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f,
    val healthRating: Double = 3.5,
    val imageUrl: String? = null,
    val ingredients: String? = null,
    val isPackaged: Boolean = false,
    val barcode: String? = null,
    val allergens: List<String> = emptyList(),
    val positivePoints: List<String> = emptyList(),
    val concernPoints: List<String> = emptyList(),
    val isHomemade: Boolean = true
)

data class LoggedMeal(
    val id: Long = 0,
    val foodId: Long = 0,
    val foodName: String,
    val category: FoodCategory = FoodCategory.ALL,
    val mealType: MealType,
    val date: String, // YYYY-MM-DD
    val timeMillis: Long = System.currentTimeMillis(),
    val servingSizeGrams: Float,
    val servingDesc: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f,
    val rating: Double = 3.5,
    val imageUrl: String? = null
)

data class DietPlan(
    val id: Long = 0,
    val weekOffset: Int = 0, // 0 = This Week, 1 = Next Week
    val dayOfWeek: Int, // 1 = Mon .. 7 = Sun
    val mealType: MealType,
    val foodName: String,
    val portionDesc: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val isCompleted: Boolean = false,
    val reason: String = "Balanced meal fitting your daily goal",
    val imageUrl: String? = null
)

data class UserProfile(
    val name: String = "User",
    val email: String = "user@nutrix.app",
    val age: Int = 25,
    val heightCm: Float = 175f,
    val weightKg: Float = 70f,
    val goalType: GoalType = GoalType.HEALTHY_WEIGHT_GAIN,
    val dietaryPreference: DietaryPreference = DietaryPreference.NON_VEGETARIAN,
    val allergies: List<String> = emptyList(),
    val dailyCalorieTarget: Int = 2000,
    val dailyWaterGlasses: Int = 8,
    val language: String = "en",
    val notificationsEnabled: Boolean = true
)

data class Reminder(
    val id: Long = 0,
    val title: String,
    val timeHour: Int, // 0..23
    val timeMinute: Int, // 0..59
    val isEnabled: Boolean = true,
    val category: String = "MEAL", // MEAL, WATER, GENERAL
    val repeatDaily: Boolean = true
)

data class GroceryItem(
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val category: String = "General",
    val isPurchased: Boolean = false
)

data class HealthRatingResult(
    val score: Double,
    val explanation: String,
    val positivePoints: List<String>,
    val concernPoints: List<String>,
    val confidence: Float = 1.0f
)

data class DayNutritionSummary(
    val date: String,
    val dayLabel: String,
    val totalCalories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatsGrams: Float,
    val fiberGrams: Float,
    val avgRating: Double? = null,
    val meals: List<LoggedMeal>
)
