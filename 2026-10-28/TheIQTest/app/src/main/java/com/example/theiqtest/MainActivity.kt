package com.example.theiqtest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theiqtest.ui.theme.TheIQTestTheme
import kotlin.jvm.java


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column() {
                Text(
                    text="What comes after this: 3,6,12,15?",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Row(verticalAlignment = CenterVertically){
                Button(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    onClick = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("17")}

                Button(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    onClick  = {
                        Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                    }) {Text("15")}
                    }
                Row() {
                    Button(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                        onClick  = {
                            val intent = Intent(this@MainActivity, Second::class.java)
                            startActivity(intent)
                        }) { Text("18") }
                    Button(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                        onClick  = {
                            Toast.makeText(this@MainActivity, "Wrong", Toast.LENGTH_SHORT).show()
                        }) { Text("16") }
                }
            }
        }
    }
}
