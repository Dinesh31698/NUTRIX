package com.nutrix.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.nutrix.app.core.database.NutrixDatabase
import com.nutrix.app.core.notifications.ReminderNotificationReceiver
import com.nutrix.app.data.repository.NutrixRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NutrixApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { NutrixDatabase.getDatabase(this) }
    val repository by lazy { NutrixRepository(database) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        applicationScope.launch {
            repository.initializeStarterDataIfEmpty()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderNotificationReceiver.CHANNEL_ID,
                "NUTRIX Meal & Water Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely alerts for meals, hydration, and nutrition logging"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
