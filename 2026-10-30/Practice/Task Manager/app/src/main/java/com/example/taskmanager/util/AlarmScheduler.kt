package com.example.taskmanager.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.taskmanager.model.Task
import com.example.taskmanager.receiver.TaskNotificationReceiver

object AlarmScheduler {

    fun scheduleTaskAlarm(context: Context, task: Task) {
        val dueDate = task.dueDate ?: return
        if (dueDate <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra(TaskNotificationReceiver.EXTRA_TITLE, task.title)
            putExtra(TaskNotificationReceiver.EXTRA_DESCRIPTION, task.description)
            putExtra(TaskNotificationReceiver.EXTRA_PRIORITY, task.priority.name)
            putExtra(TaskNotificationReceiver.EXTRA_NOTIFICATION_ID, task.id.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    dueDate,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    dueDate,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelTaskAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
