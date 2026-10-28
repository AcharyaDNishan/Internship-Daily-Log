package com.example.iqtest

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        val image = painterResource(R.drawable.bg_compose_background)
        Image(painter = image, contentDescription = null)
        Text(
            text="Jetpack Compose Tutorial",
            modifier = Modifier
                .padding(16.dp)
        )
        Text(
            text="Jetpack Compose is a modern toolkit for building native Android UI. Compose simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs.",
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .padding(start = 16.dp,end = 16.dp)
        )
        Text(
            text="In this tutorial, you build a simple UI component with declarative functions. You call Compose functions to say what elements you want and the Compose compiler does the rest. Compose is built around Composable functions. These functions let you define your app\\'s UI programmatically because they let you describe how it should look and provide data dependencies, rather than focus on the process of the UI\\'s construction, such as initializing an element and then attaching it to a parent. To create a Composable function, you add the @Composable annotation to the function name.\n",
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .padding(16.dp)
        )
        Button(
            onClick = {
                Inten
            },
        )
    }
}
