
package com.nishan.eggtimer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.mutableStateListOf
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
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .clickable {
                                        navController.navigate("details/300000")
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row {
                                    Column(
                                        modifier = Modifier.size(170.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.very_soft_boiled
                                            ),
                                            contentDescription = "Very soft boiled egg",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(150.dp)
                                        )
                                        Text("Very Soft Boiled")
                                    }
                                    Column(
                                        modifier = Modifier.size(170.dp)
                                            .clickable {
                                            navController.navigate("details/360000")
                                        },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.soft_boiled
                                            ),
                                            contentDescription = "Soft boiled egg",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(150.dp)

                                        )
                                        Text("Soft Boiled")
                                    }
                                }

                                Row {
                                    Column(
                                        modifier = Modifier.size(170.dp)
                                            .clickable {
                                                navController.navigate("details/540000")
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.medium_boiled
                                            ),
                                            contentDescription = "Medium boiled egg",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(150.dp)
                                        )
                                        Text("Medium Boiled")
                                    }
                                    Column(
                                        modifier = Modifier.size(170.dp)
                                            .clickable {
                                                navController.navigate("details/720000")
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.hard_boiled
                                            ),
                                            contentDescription = "Hard boiled egg",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(150.dp)
                                        )
                                        Text("Hard Boiled")
                                    }
                                }
                            }
                        }
                        composable(
                            route = "details/{ms}",
                            arguments = listOf(
                                navArgument("ms") {
                                    type = NavType.LongType
                                }
                            )
                        ) { backStackEntry ->
                            val initialSeconds =
                                backStackEntry.arguments?.getLong("ms") ?: 0L
                            TimerScreen(
                                totalms = initialSeconds,
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
fun TimerScreen(
    totalms: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
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
        while (msLeft > 0L) {
            delay(1000L)
            msLeft = (msLeft - 1000L).coerceAtLeast(0L)
        }
        if (totalms > 0L) {
            sendEggReadyNotification(context)
        }
    }
    val totalSeconds = msLeft / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formattedTime = String.format(
        "%02d:%02d",
        minutes,
        seconds
    )
    val angles = remember { mutableStateListOf<Float>(90F) }
    val strokeWidth = 5.dp
    val x1 = 0F
    val x2 = 100F
    val modifier = Modifier
    val ts=60
    LaunchedEffect(ts) {
        while (true) {
            angles.clear()
            var currentAngle = ts.toFloat()
            repeat(60) {
                angles.add(currentAngle)
                delay(1000)
                currentAngle = (currentAngle + 6F) % 360F
            }
        }
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        for (angle in angles) {
            rotate(degrees = angle) {
                drawLine(
                    color = Color.Blue,
                    start = Offset(x = x1, y = size.height / 2f),
                    end = Offset(x = x2, y = size.height / 2f),
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
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
            onClick = onBack,
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
        .setContentText("Time's up! Your egg is ready!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
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