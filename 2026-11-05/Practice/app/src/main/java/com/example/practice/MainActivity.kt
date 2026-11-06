package com.example.practice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var resultText by remember { mutableStateOf("Initializing Python...") }
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    try {
                        if (!Python.isStarted()) {
                            Python.start(AndroidPlatform(applicationContext))
                        }
                        val py = Python.getInstance()
                        val module = py.getModule("my_python")
                        val answer = module.callAttr(
                            "multiply", 10, 20,
                        ).toInt()
                        val greeting = module.callAttr(
                            "greet", "Kotlin",
                        ).toString()
                        Log.d("PYTHON", "Answer: $answer")
                        Log.d("PYTHON", greeting)
                        resultText = "Answer: $answer\n$greeting"
                    } catch (e: Exception) {
                        Log.e("PYTHON", "Python error", e)
                        resultText = "Error: ${e.message}"
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = resultText)
            }
        }
    }
}