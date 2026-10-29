package com.nutrix.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FoodEntity::class,
        LoggedMealEntity::class,
        DietPlanEntity::class,
        UserProfileEntity::class,
        GroceryEntity::class,
        WaterLogEntity::class,
        ReminderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NutrixDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun loggedMealDao(): LoggedMealDao
    abstract fun dietPlanDao(): DietPlanDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun groceryDao(): GroceryDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: NutrixDatabase? = null

        fun getDatabase(context: Context): NutrixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutrixDatabase::class.java,
                    "nutrix_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
