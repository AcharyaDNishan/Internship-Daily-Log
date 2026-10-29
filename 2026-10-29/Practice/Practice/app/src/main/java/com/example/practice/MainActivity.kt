package com.example.practice

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column {
                Text("")
                TextField(
                    state = rememberTextFieldState(),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text("Label") },
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    stringResource(id = R.string.app_name)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color.Blue,
                    ),
                    modifier = Modifier
                        .size(width = 240.dp, height = 100.dp)
                ) {
                    Text(
                        text = "Filled",
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
    }