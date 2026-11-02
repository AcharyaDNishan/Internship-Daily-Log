package com.example.taskmanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskmanager.model.Priority
import com.example.taskmanager.util.NotificationHelper

class TaskNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Task Reminder"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: ""
        val priorityName = intent.getStringExtra(EXTRA_PRIORITY) ?: Priority.MEDIUM.name
        val priority = try {
            Priority.valueOf(priorityName)
        } catch (e: Exception) {
            Priority.MEDIUM
        }
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, System.currentTimeMillis().toInt())

        NotificationHelper.showTaskNotification(
            context = context,
            title = "Reminder: $title",
            description = description,
            priority = priority,
            notificationId = notificationId
        )
    }

    companion object {
        const val EXTRA_TITLE = "extra_task_title"
        const val EXTRA_DESCRIPTION = "extra_task_description"
        const val EXTRA_PRIORITY = "extra_task_priority"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
