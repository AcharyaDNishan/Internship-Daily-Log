package com.example.taskmanager.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class Priority(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
) {
    val formattedDate: String
        get() {
            val formatter = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
            return formatter.format(Date(createdAt))
        }

    val formattedDueDate: String?
        get() {
            return dueDate?.let {
                val formatter = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
                formatter.format(Date(it))
            }
        }

    val isOverdue: Boolean
        get() = dueDate != null && !isCompleted && dueDate < System.currentTimeMillis()
}
