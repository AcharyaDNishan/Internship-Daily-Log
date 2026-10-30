package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TaskUiState(
    val tasks: List<Task> = listOf(
        Task(
            title = "Complete Android Basics Codelab",
            description = "Finish the Task Manager practice problem",
            isCompleted = false,
            priority = Priority.HIGH
        ),
        Task(
            title = "Review Jetpack Compose Navigation",
            description = "Learn NavHost, ViewModel, and Scaffold navigation",
            isCompleted = false,
            priority = Priority.MEDIUM
        ),
        Task(
            title = "Set up Task Notifications",
            description = "Test POST_NOTIFICATIONS runtime permission and channel",
            isCompleted = true,
            priority = Priority.LOW
        )
    ),
    val searchQuery: String = "",
    val selectedPriorityFilter: Priority? = null,
    val userMessage: String? = null,
    val lastDeletedTask: Task? = null
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

    fun addTask(title: String, description: String, priority: Priority) {
        val newTask = Task(
            title = title,
            description = description,
            isCompleted = false,
            priority = priority
        )
        _uiState.update { currentState ->
            currentState.copy(
                tasks = listOf(newTask) + currentState.tasks,
                userMessage = "Task '$title' created!"
            )
        }
    }

    fun toggleTaskCompletion(taskId: String) {
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
            currentState.copy(tasks = updatedTasks, userMessage = updatedMessage)
        }
    }

    fun deleteTask(taskId: String) {
        _uiState.update { currentState ->
            val deletedTask = currentState.tasks.find { it.id == taskId }
            val updatedTasks = currentState.tasks.filterNot { it.id == taskId }
            currentState.copy(
                tasks = updatedTasks,
                lastDeletedTask = deletedTask,
                userMessage = deletedTask?.let { "Deleted '${it.title}'" }
            )
        }
    }

    fun undoDelete() {
        _uiState.update { currentState ->
            val restoredTask = currentState.lastDeletedTask
            if (restoredTask != null) {
                currentState.copy(
                    tasks = listOf(restoredTask) + currentState.tasks,
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
