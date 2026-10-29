package com.nutrix.app.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods ORDER BY name ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE category = :category ORDER BY name ASC")
    fun getFoodsByCategory(category: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    suspend fun getFoodById(id: Long): FoodEntity?

    @Query("SELECT * FROM foods WHERE barcode = :barcode LIMIT 1")
    suspend fun getFoodByBarcode(barcode: String): FoodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodEntity>)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun countFoods(): Int
}

@Dao
interface LoggedMealDao {
    @Query("SELECT * FROM logged_meals WHERE date = :date ORDER BY timeMillis ASC")
    fun getMealsForDate(date: String): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, timeMillis ASC")
    fun getMealsBetweenDates(startDate: String, endDate: String): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals ORDER BY date DESC, timeMillis DESC LIMIT :limit")
    fun getRecentMeals(limit: Int): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals ORDER BY date DESC, timeMillis DESC")
    fun getAllMeals(): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Long): LoggedMealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: LoggedMealEntity): Long

    @Update
    suspend fun updateMeal(meal: LoggedMealEntity)

    @Query("DELETE FROM logged_meals WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    @Query("SELECT COUNT(*) FROM logged_meals")
    suspend fun countMeals(): Int
}

@Dao
interface DietPlanDao {
    @Query("SELECT * FROM diet_plans WHERE weekOffset = :weekOffset AND dayOfWeek = :dayOfWeek ORDER BY id ASC")
    fun getPlansForDay(weekOffset: Int, dayOfWeek: Int): Flow<List<DietPlanEntity>>

    @Query("SELECT * FROM diet_plans WHERE weekOffset = :weekOffset ORDER BY dayOfWeek ASC, id ASC")
    fun getPlansForWeek(weekOffset: Int): Flow<List<DietPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DietPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<DietPlanEntity>)

    @Update
    suspend fun updatePlan(plan: DietPlanEntity)

    @Query("UPDATE diet_plans SET isCompleted = :completed WHERE id = :id")
    suspend fun setPlanCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM diet_plans WHERE id = :id")
    suspend fun deletePlanById(id: Long)

    @Query("DELETE FROM diet_plans WHERE weekOffset = :weekOffset")
    suspend fun clearWeekPlans(weekOffset: Int)

    @Query("SELECT COUNT(*) FROM diet_plans")
    suspend fun countPlans(): Int
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)
}

@Dao
interface GroceryDao {
    @Query("SELECT * FROM groceries ORDER BY isPurchased ASC, id DESC")
    fun getAllGroceries(): Flow<List<GroceryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrocery(item: GroceryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceries(items: List<GroceryEntity>)

    @Update
    suspend fun updateGrocery(item: GroceryEntity)

    @Query("DELETE FROM groceries WHERE id = :id")
    suspend fun deleteGrocery(id: Long)

    @Query("DELETE FROM groceries WHERE isPurchased = 1")
    suspend fun clearPurchased()
}

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE date = :date LIMIT 1")
    fun getWaterLog(date: String): Flow<WaterLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setWaterLog(waterLog: WaterLogEntity)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY timeHour ASC, timeMinute ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun setReminderEnabled(id: Long, enabled: Boolean)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    @Query("SELECT COUNT(*) FROM reminders")
    suspend fun countReminders(): Int
}
