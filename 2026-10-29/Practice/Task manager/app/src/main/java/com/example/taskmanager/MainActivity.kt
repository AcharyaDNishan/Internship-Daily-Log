package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanager.ui.theme.TaskManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent() {
            Column {
                LazyColumn {
                    item{Box(Modifier.size(20.dp, 300.dp).background(Color.Red))}
                    item{Box(Modifier.size(20.dp, 300.dp).background(Color.Yellow))}
                    item{Box(Modifier.size(20.dp, 300.dp).background(Color.Green))}
                    item{
                        LazyRow {
                            item{Box(Modifier.size(200.dp, 30.dp).background(Color.Red))}
                            item{Box(Modifier.size(200.dp, 30.dp).background(Color.Yellow))}
                            item{Box(Modifier.size(200.dp, 30.dp).background(Color.Green))}

                        }}
                }
            }
        }
    }
}
