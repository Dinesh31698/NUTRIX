package com.nutrix.app.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val category: String,
    val servingSizeGrams: Float,
    val servingUnit: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float,
    val sugar: Float,
    val sodium: Float,
    val healthRating: Double,
    val imageUrl: String? = null,
    val ingredients: String? = null,
    val isPackaged: Boolean,
    val barcode: String? = null,
    val allergensJson: String = "[]",
    val positivePointsJson: String = "[]",
    val concernPointsJson: String = "[]",
    val isHomemade: Boolean = true
)

@Entity(
    tableName = "logged_meals",
    indices = [Index(value = ["date"])]
)
data class LoggedMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodId: Long,
    val foodName: String,
    val category: String,
    val mealType: String,
    val date: String, // YYYY-MM-DD
    val timeMillis: Long,
    val servingSizeGrams: Float,
    val servingDesc: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val fiber: Float,
    val sugar: Float,
    val sodium: Float,
    val rating: Double,
    val imageUrl: String? = null
)

@Entity(
    tableName = "diet_plans",
    indices = [Index(value = ["weekOffset", "dayOfWeek"])]
)
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekOffset: Int, // 0 = This Week, 1 = Next Week
    val dayOfWeek: Int, // 1..7 (Mon..Sun)
    val mealType: String,
    val foodName: String,
    val portionDesc: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val isCompleted: Boolean,
    val reason: String,
    val imageUrl: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val goalType: String,
    val dietaryPreference: String,
    val allergiesJson: String,
    val dailyCalorieTarget: Int,
    val dailyWaterGlasses: Int,
    val language: String,
    val notificationsEnabled: Boolean
)

@Entity(tableName = "groceries")
data class GroceryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: String,
    val category: String,
    val isPurchased: Boolean
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey val date: String,
    val glassesDrank: Int
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timeHour: Int,
    val timeMinute: Int,
    val isEnabled: Boolean = true,
    val category: String = "MEAL",
    val repeatDaily: Boolean = true
)
