package com.nutrix.app.data.repository

import com.nutrix.app.core.database.*
import com.nutrix.app.core.engine.DietSuggestionEngine
import com.nutrix.app.core.engine.HealthRatingEngine
import com.nutrix.app.core.engine.NutritionCalculator
import com.nutrix.app.core.model.*
import com.nutrix.app.core.network.OpenFoodFactsApi
import com.nutrix.app.data.seed.StarterFoodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NutrixRepository(
    private val database: NutrixDatabase,
    private val offApi: OpenFoodFactsApi = OpenFoodFactsApi.create()
) {
    private val foodDao = database.foodDao()
    private val loggedMealDao = database.loggedMealDao()
    private val dietPlanDao = database.dietPlanDao()
    private val userProfileDao = database.userProfileDao()
    private val groceryDao = database.groceryDao()
    private val waterLogDao = database.waterLogDao()
    private val reminderDao = database.reminderDao()

    suspend fun initializeStarterDataIfEmpty() = withContext(Dispatchers.IO) {
        if (foodDao.countFoods() == 0) {
            foodDao.insertFoods(StarterFoodData.items)
        }

        if (dietPlanDao.countPlans() == 0) {
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
            dietPlanDao.insertPlans(thisWeek + nextWeek)
        }

        if (reminderDao.countReminders() == 0) {
            reminderDao.insertReminders(
                listOf(
                    ReminderEntity(title = "Breakfast Reminder", timeHour = 8, timeMinute = 30, isEnabled = true, category = "MEAL"),
                    ReminderEntity(title = "Lunch Reminder", timeHour = 13, timeMinute = 30, isEnabled = true, category = "MEAL"),
                    ReminderEntity(title = "Evening Snack Reminder", timeHour = 17, timeMinute = 30, isEnabled = true, category = "MEAL"),
                    ReminderEntity(title = "Dinner Reminder", timeHour = 20, timeMinute = 30, isEnabled = true, category = "MEAL"),
                    ReminderEntity(title = "Water Hydration Alert", timeHour = 11, timeMinute = 0, isEnabled = true, category = "WATER")
                )
            )
        }

        if (loggedMealDao.countMeals() == 0) {
            val todayIso = NutritionCalculator.getTodayIsoDate()
            val yesterdayIso = NutritionCalculator.getIsoDateForDayOffset(-1)

            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 1,
                    foodName = "Lay's Classic Potato Chips",
                    category = FoodCategory.PACKAGED.name,
                    mealType = MealType.BREAKFAST.name,
                    date = todayIso,
                    timeMillis = System.currentTimeMillis() - 14400000,
                    servingSizeGrams = 52f,
                    servingDesc = "1 pack (52 g)",
                    calories = 285,
                    protein = 3.5f,
                    carbs = 30f,
                    fats = 18f,
                    fiber = 2f,
                    sugar = 1f,
                    sodium = 170f,
                    rating = 2.5
                )
            )
            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 2,
                    foodName = "Fresh Banana",
                    category = FoodCategory.FRUITS.name,
                    mealType = MealType.BREAKFAST.name,
                    date = todayIso,
                    timeMillis = System.currentTimeMillis() - 10800000,
                    servingSizeGrams = 120f,
                    servingDesc = "1 medium (120 g)",
                    calories = 105,
                    protein = 1.3f,
                    carbs = 27f,
                    fats = 0.3f,
                    fiber = 3.1f,
                    sugar = 14f,
                    sodium = 1f,
                    rating = 4.7
                )
            )
            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 3,
                    foodName = "Chicken Curry",
                    category = FoodCategory.CURRIES.name,
                    mealType = MealType.LUNCH.name,
                    date = todayIso,
                    timeMillis = System.currentTimeMillis() - 7200000,
                    servingSizeGrams = 200f,
                    servingDesc = "1 cup (200 g)",
                    calories = 320,
                    protein = 28f,
                    carbs = 12f,
                    fats = 20f,
                    fiber = 2f,
                    sugar = 4f,
                    sodium = 650f,
                    rating = 3.8
                )
            )
            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 4,
                    foodName = "Coca-Cola Can",
                    category = FoodCategory.BEVERAGES.name,
                    mealType = MealType.EVENING_SNACK.name,
                    date = todayIso,
                    timeMillis = System.currentTimeMillis() - 3600000,
                    servingSizeGrams = 330f,
                    servingDesc = "1 can (330 ml)",
                    calories = 140,
                    protein = 0f,
                    carbs = 35f,
                    fats = 0f,
                    fiber = 0f,
                    sugar = 35f,
                    sodium = 15f,
                    rating = 1.5
                )
            )

            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 5,
                    foodName = "Dark Chocolate 70%",
                    category = FoodCategory.SNACKS.name,
                    mealType = MealType.BREAKFAST.name,
                    date = yesterdayIso,
                    timeMillis = System.currentTimeMillis() - 90000000,
                    servingSizeGrams = 50f,
                    servingDesc = "1 bar (50 g)",
                    calories = 290,
                    protein = 4.2f,
                    carbs = 22f,
                    fats = 21f,
                    fiber = 5f,
                    sugar = 14f,
                    sodium = 10f,
                    rating = 3.8
                )
            )
            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 6,
                    foodName = "Fresh Whole Apple",
                    category = FoodCategory.FRUITS.name,
                    mealType = MealType.LUNCH.name,
                    date = yesterdayIso,
                    timeMillis = System.currentTimeMillis() - 80000000,
                    servingSizeGrams = 180f,
                    servingDesc = "1 medium (180 g)",
                    calories = 95,
                    protein = 0.5f,
                    carbs = 25f,
                    fats = 0.3f,
                    fiber = 4.4f,
                    sugar = 19f,
                    sodium = 2f,
                    rating = 4.8
                )
            )
            loggedMealDao.insertMeal(
                LoggedMealEntity(
                    foodId = 7,
                    foodName = "Chicken Biryani",
                    category = FoodCategory.RICE_ITEMS.name,
                    mealType = MealType.DINNER.name,
                    date = yesterdayIso,
                    timeMillis = System.currentTimeMillis() - 70000000,
                    servingSizeGrams = 350f,
                    servingDesc = "1 plate (350 g)",
                    calories = 580,
                    protein = 32f,
                    carbs = 65f,
                    fats = 22f,
                    fiber = 4f,
                    sugar = 3f,
                    sodium = 780f,
                    rating = 3.7
                )
            )
        }

        val todayStr = NutritionCalculator.getTodayIsoDate()
        waterLogDao.setWaterLog(WaterLogEntity(date = todayStr, glassesDrank = 4))
    }

    fun getAllFoods(): Flow<List<FoodItem>> = foodDao.getAllFoods().map { entities ->
        entities.map { it.toDomain() }
    }

    fun searchFoods(query: String): Flow<List<FoodItem>> = foodDao.searchFoods(query).map { entities ->
        entities.map { it.toDomain() }
    }

    fun getFoodsByCategory(category: FoodCategory): Flow<List<FoodItem>> {
        return if (category == FoodCategory.ALL) {
            getAllFoods()
        } else {
            foodDao.getFoodsByCategory(category.name).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    suspend fun getFoodById(id: Long): FoodItem? = withContext(Dispatchers.IO) {
        foodDao.getFoodById(id)?.toDomain()
    }

    suspend fun getFoodByBarcode(barcode: String): FoodItem? = withContext(Dispatchers.IO) {
        val local = foodDao.getFoodByBarcode(barcode)
        if (local != null) return@withContext local.toDomain()

        try {
            val response = offApi.getProductByBarcode(barcode)
            val p = response.product
            if (response.status == 1 && p != null) {
                val cal = p.nutriments?.energyKcal100g?.toInt() ?: 200
                val prot = p.nutriments?.proteins100g ?: 5f
                val carbs = p.nutriments?.carbohydrates100g ?: 20f
                val fat = p.nutriments?.fat100g ?: 10f
                val fib = p.nutriments?.fiber100g ?: 1f
                val sug = p.nutriments?.sugars100g ?: 5f
                val sod = (p.nutriments?.sodium100g ?: 0.2f) * 1000f

                val ratingResult = HealthRatingEngine.calculateRating(
                    category = FoodCategory.PACKAGED,
                    calories = cal,
                    proteinGrams = prot,
                    carbsGrams = carbs,
                    fatsGrams = fat,
                    fiberGrams = fib,
                    sugarGrams = sug,
                    sodiumMg = sod,
                    isPackaged = true
                )

                val entity = FoodEntity(
                    name = p.productName ?: "Scanned Product",
                    brand = p.brands ?: "Packaged",
                    category = FoodCategory.PACKAGED.name,
                    servingSizeGrams = 100f,
                    servingUnit = p.servingSize ?: "100 g",
                    calories = cal,
                    protein = prot,
                    carbs = carbs,
                    fats = fat,
                    fiber = fib,
                    sugar = sug,
                    sodium = sod,
                    healthRating = ratingResult.score,
                    imageUrl = p.imageFrontUrl,
                    ingredients = p.ingredientsText,
                    isPackaged = true,
                    barcode = barcode,
                    allergensJson = "[]",
                    positivePointsJson = "[]",
                    concernPointsJson = "[]",
                    isHomemade = false
                )
                foodDao.insertFood(entity)
                return@withContext entity.toDomain()
            }
        } catch (e: Exception) {
            // Network fallback
        }
        null
    }

    suspend fun saveCustomFood(food: FoodItem): Long = withContext(Dispatchers.IO) {
        foodDao.insertFood(food.toEntity())
    }

    fun getMealsForDate(date: String): Flow<List<LoggedMeal>> = loggedMealDao.getMealsForDate(date).map { entities ->
        entities.map { it.toDomain() }
    }

    fun getAllMeals(): Flow<List<LoggedMeal>> = loggedMealDao.getAllMeals().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun logMeal(meal: LoggedMeal): Long = withContext(Dispatchers.IO) {
        loggedMealDao.insertMeal(meal.toEntity())
    }

    suspend fun updateMeal(meal: LoggedMeal) = withContext(Dispatchers.IO) {
        loggedMealDao.updateMeal(meal.toEntity())
    }

    suspend fun deleteMeal(id: Long) = withContext(Dispatchers.IO) {
        loggedMealDao.deleteMealById(id)
    }

    fun getDietPlansForDay(weekOffset: Int, dayOfWeek: Int): Flow<List<DietPlan>> =
        dietPlanDao.getPlansForDay(weekOffset, dayOfWeek).map { entities ->
            entities.map { it.toDomain() }
        }

    fun getDietPlansForWeek(weekOffset: Int): Flow<List<DietPlan>> =
        dietPlanDao.getPlansForWeek(weekOffset).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun setPlanCompleted(id: Long, completed: Boolean) = withContext(Dispatchers.IO) {
        dietPlanDao.setPlanCompleted(id, completed)
    }

    suspend fun regenerateWeekPlan(weekOffset: Int, goal: GoalType, preference: DietaryPreference, allergies: List<String>) =
        withContext(Dispatchers.IO) {
            dietPlanDao.clearWeekPlans(weekOffset)
            val plans = DietSuggestionEngine.generateWeekPlan(weekOffset, goal, preference, allergies)
            dietPlanDao.insertPlans(plans)
        }

    suspend fun addPlannedMeal(plan: DietPlan): Long = withContext(Dispatchers.IO) {
        dietPlanDao.insertPlan(plan.toEntity())
    }

    suspend fun deletePlan(id: Long) = withContext(Dispatchers.IO) {
        dietPlanDao.deletePlanById(id)
    }

    fun getUserProfile(): Flow<UserProfile> = userProfileDao.getUserProfile().map { entity ->
        entity?.toDomain() ?: UserProfile()
    }

    suspend fun updateUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdate(profile.toEntity())
    }

    fun getGroceries(): Flow<List<GroceryItem>> = groceryDao.getAllGroceries().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addGrocery(item: GroceryItem): Long = withContext(Dispatchers.IO) {
        groceryDao.insertGrocery(item.toEntity())
    }

    suspend fun updateGrocery(item: GroceryItem) = withContext(Dispatchers.IO) {
        groceryDao.updateGrocery(item.toEntity())
    }

    suspend fun deleteGrocery(id: Long) = withContext(Dispatchers.IO) {
        groceryDao.deleteGrocery(id)
    }

    suspend fun clearPurchasedGroceries() = withContext(Dispatchers.IO) {
        groceryDao.clearPurchased()
    }

    fun getWaterLog(date: String): Flow<Int> = waterLogDao.getWaterLog(date).map {
        it?.glassesDrank ?: 0
    }

    suspend fun setWaterGlasses(date: String, glasses: Int) = withContext(Dispatchers.IO) {
        waterLogDao.setWaterLog(WaterLogEntity(date = date, glassesDrank = glasses.coerceAtLeast(0)))
    }

    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveReminder(reminder: Reminder): Long = withContext(Dispatchers.IO) {
        reminderDao.insertReminder(reminder.toEntity())
    }

    suspend fun updateReminder(reminder: Reminder) = withContext(Dispatchers.IO) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    suspend fun setReminderEnabled(id: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        reminderDao.setReminderEnabled(id, enabled)
    }

    suspend fun deleteReminder(id: Long) = withContext(Dispatchers.IO) {
        reminderDao.deleteReminder(id)
    }

    private fun FoodEntity.toDomain() = FoodItem(
        id = id,
        name = name,
        brand = brand,
        category = try { FoodCategory.valueOf(category) } catch (e: Exception) { FoodCategory.ALL },
        servingSizeGrams = servingSizeGrams,
        servingUnit = servingUnit,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        healthRating = healthRating,
        imageUrl = imageUrl,
        ingredients = ingredients,
        isPackaged = isPackaged,
        barcode = barcode,
        isHomemade = isHomemade
    )

    private fun FoodItem.toEntity() = FoodEntity(
        id = id,
        name = name,
        brand = brand,
        category = category.name,
        servingSizeGrams = servingSizeGrams,
        servingUnit = servingUnit,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        healthRating = healthRating,
        imageUrl = imageUrl,
        ingredients = ingredients,
        isPackaged = isPackaged,
        barcode = barcode,
        isHomemade = isHomemade
    )

    private fun LoggedMealEntity.toDomain() = LoggedMeal(
        id = id,
        foodId = foodId,
        foodName = foodName,
        category = try { FoodCategory.valueOf(category) } catch (e: Exception) { FoodCategory.ALL },
        mealType = try { MealType.valueOf(mealType) } catch (e: Exception) { MealType.LUNCH },
        date = date,
        timeMillis = timeMillis,
        servingSizeGrams = servingSizeGrams,
        servingDesc = servingDesc,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        rating = rating,
        imageUrl = imageUrl
    )

    private fun LoggedMeal.toEntity() = LoggedMealEntity(
        id = id,
        foodId = foodId,
        foodName = foodName,
        category = category.name,
        mealType = mealType.name,
        date = date,
        timeMillis = timeMillis,
        servingSizeGrams = servingSizeGrams,
        servingDesc = servingDesc,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        rating = rating,
        imageUrl = imageUrl
    )

    private fun DietPlanEntity.toDomain() = DietPlan(
        id = id,
        weekOffset = weekOffset,
        dayOfWeek = dayOfWeek,
        mealType = try { MealType.valueOf(mealType) } catch (e: Exception) { MealType.LUNCH },
        foodName = foodName,
        portionDesc = portionDesc,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        isCompleted = isCompleted,
        reason = reason,
        imageUrl = imageUrl
    )

    private fun DietPlan.toEntity() = DietPlanEntity(
        id = id,
        weekOffset = weekOffset,
        dayOfWeek = dayOfWeek,
        mealType = mealType.name,
        foodName = foodName,
        portionDesc = portionDesc,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fats = fats,
        isCompleted = isCompleted,
        reason = reason,
        imageUrl = imageUrl
    )

    private fun UserProfileEntity.toDomain(): UserProfile {
        val allergyList = if (allergiesJson.isBlank() || allergiesJson == "[]") {
            emptyList()
        } else {
            allergiesJson.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        }
        return UserProfile(
            name = name,
            email = email,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            goalType = try { GoalType.valueOf(goalType) } catch (e: Exception) { GoalType.HEALTHY_WEIGHT_GAIN },
            dietaryPreference = try { DietaryPreference.valueOf(dietaryPreference) } catch (e: Exception) { DietaryPreference.NON_VEGETARIAN },
            allergies = allergyList,
            dailyCalorieTarget = dailyCalorieTarget,
            dailyWaterGlasses = dailyWaterGlasses,
            language = language,
            notificationsEnabled = notificationsEnabled
        )
    }

    private fun UserProfile.toEntity(): UserProfileEntity {
        val allergyJson = "[${allergies.joinToString(",") { "\"$it\"" }}]"
        return UserProfileEntity(
            id = 1,
            name = name,
            email = email,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            goalType = goalType.name,
            dietaryPreference = dietaryPreference.name,
            allergiesJson = allergyJson,
            dailyCalorieTarget = dailyCalorieTarget,
            dailyWaterGlasses = dailyWaterGlasses,
            language = language,
            notificationsEnabled = notificationsEnabled
        )
    }

    private fun GroceryEntity.toDomain() = GroceryItem(
        id = id,
        name = name,
        quantity = quantity,
        category = category,
        isPurchased = isPurchased
    )

    private fun GroceryItem.toEntity() = GroceryEntity(
        id = id,
        name = name,
        quantity = quantity,
        category = category,
        isPurchased = isPurchased
    )

    private fun ReminderEntity.toDomain() = Reminder(
        id = id,
        title = title,
        timeHour = timeHour,
        timeMinute = timeMinute,
        isEnabled = isEnabled,
        category = category,
        repeatDaily = repeatDaily
    )

    private fun Reminder.toEntity() = ReminderEntity(
        id = id,
        title = title,
        timeHour = timeHour,
        timeMinute = timeMinute,
        isEnabled = isEnabled,
        category = category,
        repeatDaily = repeatDaily
    )
}
