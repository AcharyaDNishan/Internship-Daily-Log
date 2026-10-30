package com.example.taskmanager

import android.Manifest
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.model.Task
import com.example.taskmanager.ui.theme.TaskManagerTheme
import com.example.taskmanager.util.NotificationHelper

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
fun TaskManagerApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Incomplete.route

    val tasks = remember {
        mutableStateListOf(
            Task(
                title = "Complete Android Basics Codelab",
                description = "Finish the Task Manager practice problem",
                isCompleted = false
            ),
            Task(
                title = "Review Jetpack Compose Navigation",
                description = "Learn NavHost and Scaffold navigation",
                isCompleted = false
            ),
            Task(
                title = "Set up Notifications",
                description = "Test POST_NOTIFICATIONS runtime permission",
                isCompleted = true
            )
        )
    }

    // Initialize Notification Channel
    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)
    }

    // Runtime Notification Permission launcher for Android 13+
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
                    tasks = tasks.filter { !it.isCompleted },
                    onToggleTask = { task ->
                        val index = tasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            tasks[index] = tasks[index].copy(isCompleted = !tasks[index].isCompleted)
                        }
                    },
                    onDeleteTask = { task ->
                        tasks.removeAll { it.id == task.id }
                    },
                    onNotifyTask = { task ->
                        requestNotificationPermissionIfNeeded()
                        NotificationHelper.showTaskNotification(
                            context = context,
                            title = task.title,
                            description = task.description
                        )
                    }
                )
            }
            composable(Screen.Completed.route) {
                CompletedTasksScreen(
                    tasks = tasks.filter { it.isCompleted },
                    onToggleTask = { task ->
                        val index = tasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            tasks[index] = tasks[index].copy(isCompleted = !tasks[index].isCompleted)
                        }
                    },
                    onDeleteTask = { task ->
                        tasks.removeAll { it.id == task.id }
                    }
                )
            }
            composable(Screen.CreateTask.route) {
                CreateTaskScreen(
                    onRequestPermission = { requestNotificationPermissionIfNeeded() },
                    onAddTask = { title, description, sendNotification ->
                        val newTask = Task(title = title, description = description, isCompleted = false)
                        tasks.add(newTask)
                        if (sendNotification) {
                            requestNotificationPermissionIfNeeded()
                            NotificationHelper.showTaskNotification(
                                context = context,
                                title = "New Task Created: $title",
                                description = description
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
fun IncompleteTasksScreen(
    tasks: List<Task>,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onNotifyTask: (Task) -> Unit
) {
    if (tasks.isEmpty()) {
        TaskCompletedEmptyState()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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

@Composable
fun CompletedTasksScreen(
    tasks: List<Task>,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
fun CreateTaskScreen(
    onRequestPermission: () -> Unit,
    onAddTask: (title: String, description: String, sendNotification: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sendNotification by remember { mutableStateOf(true) }
    var titleError by remember { mutableStateOf(false) }

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
        AnimatedVisibility(visible = titleError) {
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
                .height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.send_notification),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = sendNotification,
                onCheckedChange = {
                    sendNotification = it
                    if (it) {
                        onRequestPermission()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                } else {
                    onAddTask(title.trim(), description.trim(), sendNotification)
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
        modifier = Modifier.fillMaxSize(),
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
