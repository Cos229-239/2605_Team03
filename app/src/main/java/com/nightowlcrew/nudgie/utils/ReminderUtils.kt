package com.nightowlcrew.nudgie.utils

import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Locale

/**
 * Shows a TimePickerDialog and schedules an exact alarm using AlarmUtils.
 *
 * @param context The context used to show the dialog and schedule the alarm.
 * @param taskDescription The description of the task to be used as the alarm label.
 * @param onTimeSelected Optional callback triggered when a time is selected, providing the formatted time string (HH:mm).
 */
fun showReminderTimePicker(
    context: Context,
    taskDescription: String,
    onTimeSelected: (String) -> Unit = {},
) {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val triggerTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            AlarmUtils.setExactAlarm(context, triggerTime, taskDescription)

            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        },
        currentHour,
        currentMinute,
        false,
    ).show()
}
