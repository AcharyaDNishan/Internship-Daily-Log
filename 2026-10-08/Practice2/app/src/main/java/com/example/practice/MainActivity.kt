package com.example.practice

import android.R.attr.type
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    private val vm by viewModels<ViewMM>()
    private val ap = AirplaneMode()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        registerReceiver(
            ap,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            PracticeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = vm.backgroundColor
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                Intent(applicationContext, RunningSer::class.java ).also{
                                    it.action = RunningSer.Actions.START.toString()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(it)
                                    } else {
                                        startService(it)
                                    }
                                }
                                 }
                        ) {
                            Text(text = "Start Activity")
                        }
                        Button(
                            onClick = {
                                Intent(applicationContext, RunningSer::class.java ).also{
                                    it.action = RunningSer.Actions.STOP.toString()
                                    startService(it)
                                }
                            }
                        ) {
                            Text(text = "Stop Activity")
                        }
                        Button(
                            onClick = {
                                vm.bchange()
                                val intent = Intent(context, SecondActivity::class.java)
                                context.startActivity(intent)
                            }
                        ) {
                            Text(text = "Second Activity")
                        }

                        Button(
                            onClick = {
                                Intent(Intent.ACTION_MAIN).also {
                                    it.`package` = "com.google.android.youtube"
                                    context.startActivity(it)
                                }
                            }
                        ) {
                            Text(text = "YouTube")
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("nishan@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Hello")
                                    putExtra(Intent.EXTRA_TEXT, "I am coming back.")
                                }
                                if (intent.resolveActivity(packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                                }
                            }
                        ) {
                            Text(text = "Send Mail")
                        }
                    }
                }
            }
        }
    }
}
