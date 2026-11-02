package com.example.taskmanager

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import com.example.taskmanager.ui.theme.TaskManagerTheme
import com.example.taskmanager.util.AlarmScheduler
import com.example.taskmanager.util.NotificationHelper
import com.example.taskmanager.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Incomplete : Screen("incomplete", "Pending Tasks", Icons.AutoMirrored.Filled.List)
    object Completed : Screen("completed", "Completed Tasks", Icons.Default.CheckCircle)
    object CreateTask : Screen("create_task", "Create Task", Icons.Default.Add)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerApp(taskViewModel: TaskViewModel = viewModel()) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Incomplete.route

    val uiState by taskViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize Notification Channel and Load Saved Tasks from File
    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)
        taskViewModel.loadSavedTasks(context)
    }

    // Snackbar messaging & Undo handler
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (uiState.lastDeletedTask != null) "Undo" else null
            )
            if (result == SnackbarResult.ActionPerformed) {
                taskViewModel.undoDelete(context)
            }
            taskViewModel.userMessageShown()
        }
    }

    // Runtime Notification Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentRoute) {
                        Screen.Incomplete.route -> stringResource(R.string.incomplete_tasks)
                        Screen.Completed.route -> stringResource(R.string.completed_tasks)
                        Screen.CreateTask.route -> stringResource(R.string.create_task)
                        else -> stringResource(R.string.app_name)
                    }
                    Text(titleText, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    if (currentRoute == Screen.CreateTask.route) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(Screen.Incomplete, Screen.Completed, Screen.CreateTask)
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != Screen.CreateTask.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateTask.route) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Task")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Incomplete.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Incomplete.route) {
                IncompleteTasksScreen(
                    tasks = uiState.incompleteTasks,
                    totalCount = uiState.totalCount,
                    completedCount = uiState.completedCount,
                    progressPercentage = uiState.progressPercentage,
                    searchQuery = uiState.searchQuery,
                    selectedPriority = uiState.selectedPriorityFilter,
                    onSearchChange = { taskViewModel.setSearchQuery(it) },
                    onPriorityFilterChange = { taskViewModel.setPriorityFilter(it) },
                    onToggleTask = { taskViewModel.toggleTaskCompletion(context, it.id) },
                    onDeleteTask = { task ->
                        AlarmScheduler.cancelTaskAlarm(context, task)
                        taskViewModel.deleteTask(context, task.id)
                    },
                    onNotifyTask = { task ->
                        requestNotificationPermissionIfNeeded()
                        NotificationHelper.showTaskNotification(
                            context = context,
                            title = task.title,
                            description = task.description,
                            priority = task.priority
                        )
                    }
                )
            }
            composable(Screen.Completed.route) {
                CompletedTasksScreen(
                    tasks = uiState.completedTasks,
                    searchQuery = uiState.searchQuery,
                    selectedPriority = uiState.selectedPriorityFilter,
                    onSearchChange = { taskViewModel.setSearchQuery(it) },
                    onPriorityFilterChange = { taskViewModel.setPriorityFilter(it) },
                    onToggleTask = { taskViewModel.toggleTaskCompletion(context, it.id) },
                    onDeleteTask = { task ->
                        AlarmScheduler.cancelTaskAlarm(context, task)
                        taskViewModel.deleteTask(context, task.id)
                    }
                )
            }
            composable(Screen.CreateTask.route) {
                CreateTaskScreen(
                    onRequestPermission = { requestNotificationPermissionIfNeeded() },
                    onAddTask = { title, description, priority, dueDate, scheduleAlarm, sendImmediateNotification ->
                        val createdTask = taskViewModel.addTask(context, title, description, priority, dueDate)

                        if (scheduleAlarm && dueDate != null) {
                            requestNotificationPermissionIfNeeded()
                            AlarmScheduler.scheduleTaskAlarm(context, createdTask)
                            Toast.makeText(context, "Alarm scheduled for task due time", Toast.LENGTH_SHORT).show()
                        }

                        if (sendImmediateNotification) {
                            requestNotificationPermissionIfNeeded()
                            NotificationHelper.showTaskNotification(
                                context = context,
                                title = "New Task: $title",
                                description = description,
                                priority = priority
                            )
                        }

                        navController.navigate(Screen.Incomplete.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskProgressCard(
    totalCount: Int,
    completedCount: Int,
    progressPercentage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedCount of $totalCount ($progressPercentage%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun SearchAndFilterHeader(
    searchQuery: String,
    selectedPriority: Priority?,
    onSearchChange: (String) -> Unit,
    onPriorityFilterChange: (Priority?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(stringResource(R.string.search_tasks)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = selectedPriority == null,
                    onClick = { onPriorityFilterChange(null) },
                    label = { Text(stringResource(R.string.all_priorities)) }
                )
            }
            items(Priority.entries.toTypedArray()) { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = {
                        onPriorityFilterChange(if (selectedPriority == priority) null else priority)
                    },
                    label = { Text(priority.label) }
                )
            }
        }
    }
}

@Composable
fun IncompleteTasksScreen(
    tasks: List<Task>,
    totalCount: Int,
    completedCount: Int,
    progressPercentage: Int,
    searchQuery: String,
    selectedPriority: Priority?,
    onSearchChange: (String) -> Unit,
    onPriorityFilterChange: (Priority?) -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onNotifyTask: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TaskProgressCard(
            totalCount = totalCount,
            completedCount = completedCount,
            progressPercentage = progressPercentage
        )

        SearchAndFilterHeader(
            searchQuery = searchQuery,
            selectedPriority = selectedPriority,
            onSearchChange = onSearchChange,
            onPriorityFilterChange = onPriorityFilterChange
        )

        if (tasks.isEmpty()) {
            TaskCompletedEmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onDelete = { onDeleteTask(task) },
                        onNotify = { onNotifyTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedTasksScreen(
    tasks: List<Task>,
    searchQuery: String,
    selectedPriority: Priority?,
    onSearchChange: (String) -> Unit,
    onPriorityFilterChange: (Priority?) -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchAndFilterHeader(
            searchQuery = searchQuery,
            selectedPriority = selectedPriority,
            onSearchChange = onSearchChange,
            onPriorityFilterChange = onPriorityFilterChange
        )

        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_completed_tasks),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onDelete = { onDeleteTask(task) },
                        onNotify = null
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onNotify: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    PriorityChip(priority = task.priority)
                }
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (task.formattedDueDate != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (task.isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.height(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.due_prefix, task.formattedDueDate!!),
                            fontSize = 12.sp,
                            fontWeight = if (task.isOverdue) FontWeight.Bold else FontWeight.Medium,
                            color = if (task.isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        if (task.isOverdue) {
                            Spacer(modifier = Modifier.width(4.dp))
                            AssistChip(
                                onClick = { },
                                label = { Text(stringResource(R.string.overdue), fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    labelColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: ${task.formattedDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            if (onNotify != null) {
                IconButton(onClick = onNotify) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Show Notification",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PriorityChip(priority: Priority) {
    val (containerColor, contentColor) = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        Priority.LOW -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    AssistChip(
        onClick = { },
        label = { Text(priority.label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        )
    )
}

@Composable
fun CreateTaskScreen(
    onRequestPermission: () -> Unit,
    onAddTask: (title: String, description: String, priority: Priority, dueDate: Long?, scheduleAlarm: Boolean, sendImmediateNotification: Boolean) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var sendImmediateNotification by remember { mutableStateOf(false) }
    var scheduleAlarm by remember { mutableStateOf(true) }
    var titleError by remember { mutableStateOf(false) }

    var selectedCalendar by remember { mutableStateOf<Calendar?>(null) }

    fun openDatePicker() {
        val calendar = selectedCalendar ?: Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance()
                newCal.set(Calendar.YEAR, year)
                newCal.set(Calendar.MONTH, month)
                newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedCalendar?.let {
                    newCal.set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                    newCal.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                }
                selectedCalendar = newCal
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun openTimePicker() {
        val calendar = selectedCalendar ?: Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = selectedCalendar ?: Calendar.getInstance()
                newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                newCal.set(Calendar.MINUTE, minute)
                newCal.set(Calendar.SECOND, 0)
                selectedCalendar = newCal
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.create_task),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (it.isNotBlank()) titleError = false
            },
            label = { Text(stringResource(R.string.task_title)) },
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        AnimatedVisibility(
            visible = titleError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "Title cannot be empty",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.task_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Priority.entries.forEach { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { selectedPriority = priority },
                    label = { Text(priority.label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.due_date),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { openDatePicker() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    selectedCalendar?.let {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it.timeInMillis))
                    } ?: stringResource(R.string.select_due_date)
                )
            }

            OutlinedButton(
                onClick = { openTimePicker() },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    selectedCalendar?.let {
                        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(it.timeInMillis))
                    } ?: stringResource(R.string.select_due_time)
                )
            }

            if (selectedCalendar != null) {
                IconButton(onClick = { selectedCalendar = null }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Due Date")
                }
            }
        }

        if (selectedCalendar != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.schedule_notification),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = scheduleAlarm,
                    onCheckedChange = {
                        scheduleAlarm = it
                        if (it) onRequestPermission()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.send_notification),
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = sendImmediateNotification,
                onCheckedChange = {
                    sendImmediateNotification = it
                    if (it) onRequestPermission()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                } else {
                    onAddTask(
                        title.trim(),
                        description.trim(),
                        selectedPriority,
                        selectedCalendar?.timeInMillis,
                        scheduleAlarm,
                        sendImmediateNotification
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add_task), fontSize = 16.sp)
        }
    }
}

@Composable
fun TaskCompletedEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_task_completed),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.all_tasks_completed),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.nice_work),
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskManagerAppPreview() {
    TaskManagerTheme {
        TaskManagerApp()
    }
}
