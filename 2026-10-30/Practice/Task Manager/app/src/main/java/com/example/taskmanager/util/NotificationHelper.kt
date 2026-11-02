package com.example.taskmanager.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.model.Priority

object NotificationHelper {
    const val CHANNEL_ID = "task_manager_channel"
    private const val CHANNEL_NAME = "Task Notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Task Manager tasks"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showTaskNotification(
        context: Context,
        title: String,
        description: String,
        priority: Priority = Priority.MEDIUM,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (description.isNotBlank()) {
            "Priority: ${priority.label} | $description"
        } else {
            "Priority: ${priority.label}"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task_completed)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(
                when (priority) {
                    Priority.HIGH -> NotificationCompat.PRIORITY_HIGH
                    Priority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
                    Priority.LOW -> NotificationCompat.PRIORITY_LOW
                }
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
