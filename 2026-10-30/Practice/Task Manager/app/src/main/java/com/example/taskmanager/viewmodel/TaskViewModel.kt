package com.example.taskmanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taskmanager.data.TaskStorageManager
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedPriorityFilter: Priority? = null,
    val userMessage: String? = null,
    val lastDeletedTask: Task? = null,
    val isLoaded: Boolean = false
) {
    val incompleteTasks: List<Task>
        get() = filterTasks(isCompleted = false)

    val completedTasks: List<Task>
        get() = filterTasks(isCompleted = true)

    val totalCount: Int
        get() = tasks.size

    val completedCount: Int
        get() = tasks.count { it.isCompleted }

    val progressPercentage: Int
        get() = if (totalCount == 0) 0 else ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt()

    private fun filterTasks(isCompleted: Boolean): List<Task> {
        return tasks.filter { task ->
            task.isCompleted == isCompleted &&
                    (searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true) || task.description.contains(searchQuery, ignoreCase = true)) &&
                    (selectedPriorityFilter == null || task.priority == selectedPriorityFilter)
        }
    }
}

class TaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun loadSavedTasks(context: Context) {
        if (!_uiState.value.isLoaded) {
            val loadedTasks = TaskStorageManager.loadTasks(context)
            _uiState.update { currentState ->
                currentState.copy(
                    tasks = loadedTasks,
                    isLoaded = true
                )
            }
        }
    }

    fun addTask(context: Context, title: String, description: String, priority: Priority, dueDate: Long? = null): Task {
        val newTask = Task(
            title = title,
            description = description,
            isCompleted = false,
            priority = priority,
            dueDate = dueDate
        )
        _uiState.update { currentState ->
            val updatedList = listOf(newTask) + currentState.tasks
            TaskStorageManager.saveTasks(context, updatedList)
            currentState.copy(
                tasks = updatedList,
                userMessage = "Task '$title' created!"
            )
        }
        return newTask
    }

    fun toggleTaskCompletion(context: Context, taskId: String) {
        _uiState.update { currentState ->
            var updatedMessage: String? = null
            val updatedTasks = currentState.tasks.map { task ->
                if (task.id == taskId) {
                    val newCompleted = !task.isCompleted
                    updatedMessage = if (newCompleted) "Task marked completed" else "Task marked pending"
                    task.copy(isCompleted = newCompleted)
                } else {
                    task
                }
            }
            TaskStorageManager.saveTasks(context, updatedTasks)
            currentState.copy(tasks = updatedTasks, userMessage = updatedMessage)
        }
    }

    fun deleteTask(context: Context, taskId: String) {
        _uiState.update { currentState ->
            val deletedTask = currentState.tasks.find { it.id == taskId }
            val updatedTasks = currentState.tasks.filterNot { it.id == taskId }
            TaskStorageManager.saveTasks(context, updatedTasks)
            currentState.copy(
                tasks = updatedTasks,
                lastDeletedTask = deletedTask,
                userMessage = deletedTask?.let { "Deleted '${it.title}'" }
            )
        }
    }

    fun undoDelete(context: Context) {
        _uiState.update { currentState ->
            val restoredTask = currentState.lastDeletedTask
            if (restoredTask != null) {
                val updatedTasks = listOf(restoredTask) + currentState.tasks
                TaskStorageManager.saveTasks(context, updatedTasks)
                currentState.copy(
                    tasks = updatedTasks,
                    lastDeletedTask = null,
                    userMessage = "Restored '${restoredTask.title}'"
                )
            } else {
                currentState
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setPriorityFilter(priority: Priority?) {
        _uiState.update { it.copy(selectedPriorityFilter = priority) }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
