package com.nishan.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.nishan.practice.ui.theme.PracticeTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = 90,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: Int, modifier: Modifier = Modifier) {
    val angles = remember { mutableStateListOf<Float>() }
    val strokeWidth = 5.dp
    val x1 = 0F
    val x2 = 100F
    LaunchedEffect(name) {
        while (true) {
            angles.clear()
            var currentAngle = name.toFloat()
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
                    color = Color.Yellow,
                    start = Offset(x = x1, y = size.height / 2f),
                    end = Offset(x = x2, y = size.height / 2f),
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
