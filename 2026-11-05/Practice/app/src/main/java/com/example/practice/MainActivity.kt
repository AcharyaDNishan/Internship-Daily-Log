package com.example.practice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread {
            try {
                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(applicationContext))
                }

                val py = Python.getInstance()

                // Access Python file
                val module = py.getModule("my_python")

                // Call Python functions
                val answer = module.callAttr(
                    "multiply", 10, 20
                ).toInt()

                val greeting = module.callAttr(
                    "greet", "Kotlin"
                ).toString()

                Log.d("PYTHON", "Answer: $answer")
                Log.d("PYTHON", greeting)

            } catch (e: Exception) {
                Log.e("PYTHON", "Python error", e)
            }
        }.start()
    }
}