package com.example.practice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    private val vm by viewModels<ViewMM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            PracticeTheme {
                Scaffold(
                    containerColor = vm.backgroundColor
                ) { innerPadding ->
                    Button(
                        onClick = {
                            vm.bchange()
                            val intent = Intent(context, SecondActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Text(text = "Click Me")
                    }
            }
                Scaffold(
                    containerColor = vm.backgroundColor
                ) {innerPadding ->
                    Button(
                        onClick = {
                            Intent(Intent.ACTION_MAIN).also{
                                it.`package`="com.google.android.youtube"
                                context.startActivity(it)
                            }
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Text(text = "Youtube")
                    }
                }
        }
    }
}
    }

