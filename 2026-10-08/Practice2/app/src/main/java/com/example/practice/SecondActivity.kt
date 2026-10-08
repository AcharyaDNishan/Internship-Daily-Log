package com.example.practice

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ComponentActivity

class SecondActivity: ComponentActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){
            IntentsAndIntentFiltersTheme{

            }
        }
    }
}