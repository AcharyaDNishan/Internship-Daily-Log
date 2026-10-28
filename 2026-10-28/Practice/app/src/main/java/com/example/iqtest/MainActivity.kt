package com.example.iqtest

import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.iqtest.ui.theme.IQTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent(){
            IQTestTheme {
                Scaffold {
                        Greeting("Chat App", modifier = Modifier.padding(it))
                    }
                }
            }
        }
    }


@Composable
fun Greeting(question: String, modifier: Modifier = Modifier) {
    Column() {
        Text(" ")
        Text(
            text="$question",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
        )
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        

    }
}
