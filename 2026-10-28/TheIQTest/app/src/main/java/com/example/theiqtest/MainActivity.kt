package com.example.theiqtest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.theiqtest.ui.theme.TheIQTestTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column() {
                Text("")
                Text("What comes after this: 3,6,12,15?")
                Button(
                    onClick = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("17")}
                Button(
                    onClick = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("15")}
                Button(
                    onClick = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("18")}

                Button(
                    onClick = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("16")}
            }
        }
    }
}
