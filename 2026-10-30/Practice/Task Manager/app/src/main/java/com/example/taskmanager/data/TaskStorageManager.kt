package com.example.taskmanager.data

import android.content.Context
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object TaskStorageManager {
    private const val FILE_NAME = "tasks_data.json"

    fun saveTasks(context: Context, tasks: List<Task>) {
        try {
            val jsonArray = JSONArray()
            for (task in tasks) {
                val jsonObject = JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("isCompleted", task.isCompleted)
                    put("priority", task.priority.name)
                    put("createdAt", task.createdAt)
                    if (task.dueDate != null) {
                        put("dueDate", task.dueDate)
                    } else {
                        put("dueDate", JSONObject.NULL)
                    }
                }
                jsonArray.put(jsonObject)
            }

            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadTasks(context: Context): List<Task> {
        val tasks = mutableListOf<Task>()
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        try {
            val jsonString = file.readText()
            if (jsonString.isBlank()) return emptyList()

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.optString("id")
                val title = jsonObject.optString("title")
                val description = jsonObject.optString("description", "")
                val isCompleted = jsonObject.optBoolean("isCompleted", false)
                val priorityName = jsonObject.optString("priority", Priority.MEDIUM.name)
                val priority = try {
                    Priority.valueOf(priorityName)
                } catch (e: Exception) {
                    Priority.MEDIUM
                }
                val createdAt = jsonObject.optLong("createdAt", System.currentTimeMillis())
                val dueDate = if (jsonObject.isNull("dueDate")) null else jsonObject.optLong("dueDate")

                if (title.isNotBlank()) {
                    tasks.add(
                        Task(
                            id = id,
                            title = title,
                            description = description,
                            isCompleted = isCompleted,
                            priority = priority,
                            createdAt = createdAt,
                            dueDate = dueDate
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }
}
