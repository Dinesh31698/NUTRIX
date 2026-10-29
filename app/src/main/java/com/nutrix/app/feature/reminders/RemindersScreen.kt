package com.nutrix.app.feature.reminders

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.GlassButton
import com.nutrix.app.core.designsystem.GlassCard
import com.nutrix.app.core.designsystem.NutrixButton
import com.nutrix.app.core.designsystem.NutrixColors
import com.nutrix.app.core.model.Reminder
import com.nutrix.app.core.notifications.ReminderScheduler
import java.util.Locale

@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    onToggleReminder: (Long, Boolean) -> Unit,
    onAddReminder: (Reminder) -> Unit,
    onDeleteReminder: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NutrixColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Reminder Settings",
                    color = NutrixColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Receive gentle alerts to stay hydrated and log your meals consistently on time.",
                color = NutrixColors.TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Custom Reminder Button
            NutrixButton(
                text = "+ Add New Reminder",
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    val formattedTime = formatTime(reminder.timeHour, reminder.timeMinute)
                    ReminderRow(
                        reminder = reminder,
                        formattedTime = formattedTime,
                        onCheckedChange = { isChecked ->
                            onToggleReminder(reminder.id, isChecked)
                            val updated = reminder.copy(isEnabled = isChecked)
                            if (isChecked) {
                                ReminderScheduler.scheduleReminder(context, updated)
                            } else {
                                ReminderScheduler.cancelReminder(context, reminder.id)
                            }
                        },
                        onDelete = {
                            ReminderScheduler.cancelReminder(context, reminder.id)
                            onDeleteReminder(reminder.id)
                        }
                    )
                }
            }
        }

        // Add Reminder Dialog
        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, hour, minute ->
                    showAddDialog = false
                    val newReminder = Reminder(
                        title = title,
                        timeHour = hour,
                        timeMinute = minute,
                        isEnabled = true,
                        category = if (title.contains("Water", ignoreCase = true)) "WATER" else "MEAL"
                    )
                    onAddReminder(newReminder)
                    ReminderScheduler.scheduleReminder(context, newReminder)
                }
            )
        }
    }
}

@Composable
private fun ReminderRow(
    reminder: Reminder,
    formattedTime: String,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (reminder.category == "WATER") NutrixColors.ElectricBlue.copy(alpha = 0.2f)
                            else NutrixColors.MintGreen.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (reminder.category == "WATER") Icons.Default.WaterDrop else Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = if (reminder.category == "WATER") NutrixColors.ElectricBlue else NutrixColors.MintGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = reminder.title,
                        color = NutrixColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$formattedTime • Daily",
                        color = NutrixColors.ElectricBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NutrixColors.TextPrimary,
                        checkedTrackColor = NutrixColors.ElectricBlue,
                        uncheckedThumbColor = NutrixColors.TextMuted,
                        uncheckedTrackColor = NutrixColors.CardBorder
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = NutrixColors.CoralRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, hour: Int, minute: Int) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("Meal Reminder") }
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(30) }

    val formattedTime = formatTime(hour, minute)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NutrixColors.CardSurface,
        title = {
            Text("Set New Reminder", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Label", color = NutrixColors.TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NutrixColors.TextPrimary,
                        unfocusedTextColor = NutrixColors.TextPrimary,
                        focusedBorderColor = NutrixColors.ElectricBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time picker button
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, selectedHour, selectedMinute ->
                                    hour = selectedHour
                                    minute = selectedMinute
                                },
                                hour,
                                minute,
                                false
                            ).show()
                        },
                    backgroundColor = NutrixColors.BackgroundSecondary,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Reminder Time", color = NutrixColors.TextSecondary, fontSize = 11.sp)
                            Text(formattedTime, color = NutrixColors.ElectricBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = NutrixColors.ElectricBlue)
                    }
                }
            }
        },
        confirmButton = {
            NutrixButton(
                text = "Save Reminder",
                enabled = title.isNotBlank(),
                onClick = { onConfirm(title.trim(), hour, minute) }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NutrixColors.TextSecondary)
            }
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when (hour % 12) {
        0 -> 12
        else -> hour % 12
    }
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, amPm)
}
