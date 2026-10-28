package com.example.iqtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                        Greeting("What names come next in the sequence:", modifier = Modifier.padding(it))
                        Greeting("What number come next in the sequence:", modifier = Modifier.padding(it))
                    }
                }
            }
        }
    }


@Composable
fun Greeting(question: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(24.dp)) {
        Text("$question")
        
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IQTestTheme {
        Greeting("Android")
    }
}