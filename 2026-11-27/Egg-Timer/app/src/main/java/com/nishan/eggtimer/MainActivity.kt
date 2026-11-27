package com.nishan.eggtimer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nishan.eggtimer.ui.theme.EggTimerTheme
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        setContent {
            EggTimerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onSelectPreset = { ms ->
                                    navController.navigate("details/$ms")
                                }
                            )
                        }
                        composable(
                            route = "details/{ms}",
                            arguments = listOf(
                                navArgument("ms") {
                                    type = NavType.LongType
                                }
                            )
                        ) { backStackEntry ->
                            val initialMs = backStackEntry.arguments?.getLong("ms") ?: 0L
                            TimerScreen(
                                totalms = initialMs,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onSelectPreset: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(160.dp)
                    .clickable { onSelectPreset(300000L) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.very_soft_boiled),
                    contentDescription = "Very soft boiled egg",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )
                Text(
                    text = "Very Soft Boiled",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier
                    .size(160.dp)
                    .clickable { onSelectPreset(360000L) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soft_boiled),
                    contentDescription = "Soft boiled egg",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )
                Text(
                    text = "Soft Boiled",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(160.dp)
                    .clickable { onSelectPreset(540000L) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.medium_boiled),
                    contentDescription = "Medium boiled egg",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )
                Text(
                    text = "Medium Boiled",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier
                    .size(160.dp)
                    .clickable { onSelectPreset(720000L) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hard_boiled),
                    contentDescription = "Hard boiled egg",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )
                Text(
                    text = "Hard Boiled",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun TimerScreen(
    totalms: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val endTimeMillis = remember(totalms) {
        System.currentTimeMillis() + totalms
    }

    var msLeft by remember(totalms) {
        mutableLongStateOf(totalms)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    LaunchedEffect(totalms) {
        if (totalms > 0L) {
            EggAlarmManager.schedule(
                context = context,
                durationMs = totalms
            )
        }
        while (msLeft > 0L) {
            delay(500L)
            msLeft = (endTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        }
        if (totalms > 0L) {
            sendEggReadyNotification(context)
        }
    }

    val totalSeconds = msLeft / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formattedTime = String.format(
        Locale.getDefault(),
        "%02d:%02d",
        minutes,
        seconds
    )

    val strokeWidth = 10.dp
    val x1 = 20F
    val x2 = 200F
    val elapsedSeconds = ((totalms - msLeft) / 1000L).toInt()
    val linesCount = if (msLeft > 0L) (elapsedSeconds % 60) + 1 else 60

    if (msLeft > 0L) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until linesCount) {
                val angle = (60F + i * 6F) % 360F
                rotate(degrees = angle) {
                    drawLine(
                        color = Color(0xFFFF7B00),
                        start = Offset(x = x1, y = size.height / 2f),
                        end = Offset(x = x2, y = size.height / 2f),
                        strokeWidth = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (msLeft > 0L) {
                formattedTime
            } else {
                "Egg is ready!"
            },
            style = MaterialTheme.typography.displayLarge
        )
        Button(
            onClick = {
                EggAlarmManager.cancel(context)
                onBack()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Cancel / Back")
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "egg_timer_channel",
            "Egg Timer Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when your boiled egg is ready"
        }
        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

@SuppressLint("MissingPermission")
fun sendEggReadyNotification(context: Context) {
    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    val notification = NotificationCompat.Builder(
        context,
        "egg_timer_channel"
    )
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Egg Timer")
        .setContentText("Your egg is ready! 🥚")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .build()

    try {
        NotificationManagerCompat.from(context).notify(
            1001,
            notification
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

class EggTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        sendEggReadyNotification(context)
    }
}

object EggAlarmManager {
    private const val REQUEST_CODE = 1001

    fun schedule(context: Context, durationMs: Long) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            EggTimerReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + durationMs

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            EggTimerReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
