package com.nutrix.app.core.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nutrix.app.MainActivity
import com.nutrix.app.R

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "NUTRIX Health Reminder"
        val reminderId = intent.getLongExtra(EXTRA_ID, 1L)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = when {
            title.contains("Breakfast", ignoreCase = true) -> "Start your day with balanced nutrition. Log your breakfast in NUTRIX! ☀️"
            title.contains("Lunch", ignoreCase = true) -> "Midday refueling time! Check your macro progress and log your lunch. 🍛"
            title.contains("Snack", ignoreCase = true) -> "Time for an energizing snack to keep your metabolism active! 🥪"
            title.contains("Dinner", ignoreCase = true) -> "Enjoy a healthy, restorative dinner and track your daily calorie target. 🍲"
            title.contains("Water", ignoreCase = true) -> "Hydration check! Drink a glass of fresh water to maintain peak metabolism. 💧"
            else -> "Stay on track with your nutrition goals today with NUTRIX."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("NUTRIX: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)
        } catch (e: SecurityException) {
            // Handled when POST_NOTIFICATIONS is not yet granted by user
        }
    }

    companion object {
        const val CHANNEL_ID = "nutrix_reminders"
        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_reminder_title"
    }
}
