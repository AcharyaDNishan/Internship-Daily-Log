package com.nishan.newsapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.nishan.newsapp.ui.theme.NewsAppTheme

private const val CHANNEL_ID = "news_channel"
private const val CHANNEL_NAME = "News Notifications"
private const val PREFS_NAME = "user_prefs"
private const val KEY_USER_NAME = "user_name"
private const val KEY_IS_LOGGED_IN = "is_logged_in"

@IgnoreExtraProperties
data class NewsItem(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val body: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// Thread-safe Singleton SQLite Database Helper
class NewsDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "news_database.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NEWS = "news"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_BODY = "body"
        const val COLUMN_TIMESTAMP = "timestamp"

        @Volatile
        private var instance: NewsDatabaseHelper? = null

        fun getInstance(context: Context): NewsDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: NewsDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NEWS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_BODY TEXT,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NEWS")
        onCreate(db)
    }

    fun insertOrUpdateNews(news: NewsItem): Boolean {
        return try {
            val values = ContentValues().apply {
                put(COLUMN_ID, news.id)
                put(COLUMN_TITLE, news.title)
                put(COLUMN_AUTHOR, news.author)
                put(COLUMN_BODY, news.body)
                put(COLUMN_TIMESTAMP, news.timestamp)
            }
            writableDatabase.insertWithOnConflict(TABLE_NEWS, null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllNews(): List<NewsItem> {
        val newsList = mutableListOf<NewsItem>()
        try {
            val cursor = readableDatabase.query(
                TABLE_NEWS,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_TIMESTAMP DESC"
            )
            cursor.use { c ->
                val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
                val titleIndex = c.getColumnIndexOrThrow(COLUMN_TITLE)
                val authorIndex = c.getColumnIndexOrThrow(COLUMN_AUTHOR)
                val bodyIndex = c.getColumnIndexOrThrow(COLUMN_BODY)
                val timestampIndex = c.getColumnIndexOrThrow(COLUMN_TIMESTAMP)

                while (c.moveToNext()) {
                    newsList.add(
                        NewsItem(
                            id = c.getString(idIndex),
                            title = c.getString(titleIndex),
                            author = c.getString(authorIndex),
                            body = c.getString(bodyIndex) ?: "",
                            timestamp = c.getLong(timestampIndex)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return newsList
    }

    fun deleteNews(newsId: String): Boolean {
        return try {
            writableDatabase.delete(TABLE_NEWS, "$COLUMN_ID = ?", arrayOf(newsId)) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            getFirebaseDatabase()?.setPersistenceEnabled(true)
        } catch (_: Throwable) {}

        createNotificationChannel(this)

        setContent {
            NewsAppTheme {
                val context = LocalContext.current
                var loggedIn by remember { mutableStateOf(isUserLoggedIn(context)) }
                var userName by remember { mutableStateOf(getUserName(context)) }

                if (loggedIn) {
                    MainScreen(
                        currentUserName = userName,
                        onLogout = {
                            logoutUser(context)
                            loggedIn = false
                            userName = ""
                        }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { name ->
                            userName = name
                            loggedIn = true
                        }
                    )
                }
            }
        }
    }
}

private fun getFirebaseDatabase(): FirebaseDatabase? {
    return try {
        FirebaseDatabase.getInstance()
    } catch (_: Throwable) {
        try {
            FirebaseDatabase.getInstance("https://news-app-a535d-default-rtdb.firebaseio.com/")
        } catch (_: Throwable) {
            null
        }
    }
}

// User preferences helper functions
private fun getUserName(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_USER_NAME, "") ?: ""
}

private fun saveUserLogin(context: Context, name: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
        .putString(KEY_USER_NAME, name)
        .putBoolean(KEY_IS_LOGGED_IN, true)
        .apply()
}

private fun logoutUser(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}

private fun isUserLoggedIn(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getUserName(context).isNotBlank()
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new news articles"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } catch (_: Throwable) {}
    }
}

private fun showNewsNotification(context: Context, title: String, author: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    try {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New News: $title")
            .setContentText("By: $author")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
    } catch (_: Throwable) {}
}

@Composable
fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { }

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to News App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Enter your name to continue",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Your Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )

        Button(
            onClick = {
                if (nameInput.isBlank()) {
                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                } else {
                    val trimmedName = nameInput.trim()
                    saveUserLogin(context, trimmedName)
                    Toast.makeText(context, "Welcome, $trimmedName!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(trimmedName)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login", fontSize = 18.sp)
        }
    }
}

@Composable
fun MainScreen(
    currentUserName: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Request notification permission on Android 13+
    RequestNotificationPermission()

    // Listen for incoming new news articles to trigger system notifications
    DisposableEffect(Unit) {
        val listenerAttachTime = System.currentTimeMillis()
        val firebaseDb = getFirebaseDatabase()

        if (firebaseDb != null) {
            val databaseRef = firebaseDb.getReference("news")

            val childListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        val item = snapshot.getValue(NewsItem::class.java)
                        if (item != null && item.timestamp >= listenerAttachTime) {
                            showNewsNotification(context, item.title, item.author)
                        }
                    } catch (_: Throwable) {}
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }

            try {
                databaseRef.addChildEventListener(childListener)
            } catch (_: Throwable) {}

            onDispose {
                try {
                    databaseRef.removeEventListener(childListener)
                } catch (_: Throwable) {}
            }
        } else {
            onDispose { }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "News") },
                    label = { Text("News") },
                    selected = currentRoute == "news",
                    onClick = {
                        if (currentRoute != "news") {
                            navController.navigate("news") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                    label = { Text("Create") },
                    selected = currentRoute == "create",
                    onClick = {
                        if (currentRoute != "create") {
                            navController.navigate("create") {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "news",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("news") {
                NewsScreen(
                    currentUserName = currentUserName,
                    onLogout = onLogout
                )
            }
            composable("create") {
                CreateNewsScreen(
                    currentUserName = currentUserName,
                    onNewsSaved = {
                        navController.navigate("news") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NewsScreen(
    currentUserName: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NewsDatabaseHelper.getInstance(context) }
    var searchQuery by remember { mutableStateOf("") }
    var allNews by remember { mutableStateOf(dbHelper.getAllNews()) }
    var isLoading by remember { mutableStateOf(false) }

    // Sync in real-time with Firebase Realtime Database and cache into local SQL database
    DisposableEffect(Unit) {
        val firebaseDb = getFirebaseDatabase()
        if (firebaseDb != null) {
            val databaseRef = firebaseDb.getReference("news")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val newsList = mutableListOf<NewsItem>()
                        for (child in snapshot.children) {
                            val item = child.getValue(NewsItem::class.java)
                            if (item != null) {
                                newsList.add(item)
                                dbHelper.insertOrUpdateNews(item)
                            }
                        }
                        allNews = dbHelper.getAllNews()
                    } catch (_: Throwable) {}
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            }

            try {
                databaseRef.addValueEventListener(listener)
            } catch (_: Throwable) {
                isLoading = false
            }

            onDispose {
                try {
                    databaseRef.removeEventListener(listener)
                } catch (_: Throwable) {}
            }
        } else {
            isLoading = false
            onDispose { }
        }
    }

    val filteredNews = allNews.filter { news ->
        searchQuery.isBlank() ||
                news.title.contains(searchQuery, ignoreCase = true) ||
                news.author.contains(searchQuery, ignoreCase = true) ||
                news.body.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "News",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Logged in as $currentUserName",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onLogout) {
                Text("Logout", color = MaterialTheme.colorScheme.error)
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Title, Author, or Content") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredNews.isEmpty()) {
            Text(
                text = if (searchQuery.isBlank()) "No news articles found. Tap Create to add one!" else "No matching news found.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredNews, key = { it.id.ifEmpty { it.title + it.timestamp } }) { news ->
                    NewsCard(
                        news = news,
                        onDeleteSuccess = {
                            allNews = dbHelper.getAllNews()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNewsScreen(
    currentUserName: String,
    onNewsSaved: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NewsDatabaseHelper.getInstance(context) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf(currentUserName) }
    var newsBody by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create News",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("News Title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = newsBody,
            onValueChange = { newsBody = it },
            label = { Text("News Content") },
            singleLine = false,
            maxLines = 10,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (title.isBlank() || author.isBlank()) {
                    Toast.makeText(context, "Please enter both Title and Author", Toast.LENGTH_SHORT).show()
                } else {
                    isSaving = true
                    val newsId = System.currentTimeMillis().toString()
                    val newsItem = NewsItem(
                        id = newsId,
                        title = title.trim(),
                        author = author.trim(),
                        body = newsBody.trim(),
                        timestamp = System.currentTimeMillis()
                    )

                    // 1. Save immediately to local SQL database
                    dbHelper.insertOrUpdateNews(newsItem)

                    // 2. Sync to Firebase Realtime Database
                    try {
                        getFirebaseDatabase()?.getReference("news")
                            ?.child(newsId)
                            ?.setValue(newsItem)
                    } catch (_: Throwable) {}

                    isSaving = false
                    Toast.makeText(context, "News saved successfully!", Toast.LENGTH_SHORT).show()
                    title = ""
                    author = ""
                    newsBody = ""
                    onNewsSaved()
                }
            },
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save News", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun NewsCard(
    news: NewsItem,
    onDeleteSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val dbHelper = remember { NewsDatabaseHelper.getInstance(context) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun deleteArticle() {
        if (news.id.isNotBlank()) {
            // Delete locally from SQL database
            dbHelper.deleteNews(news.id)
            onDeleteSuccess()

            // Delete remotely from Firebase
            try {
                getFirebaseDatabase()?.getReference("news")
                    ?.child(news.id)
                    ?.removeValue()
            } catch (_: Throwable) {}

            Toast.makeText(context, "News deleted!", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = news.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showDeleteConfirm = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete News",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "By: ${news.author}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (news.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = news.body,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tap to read full news",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = news.title,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "By: ${news.author}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = news.body.ifBlank { "No content available." },
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        showDeleteConfirm = true
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete News") },
            text = { Text("Are you sure you want to delete \"${news.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        deleteArticle()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
