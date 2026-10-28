package com.example.iqtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Third : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column() {
                Row() {
                    Text(
                        text = "One Box",
                        modifier = Modifier
                            .width(250.dp)
                            .padding(16.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()
                            .clickable(onClick = {})
                    )
                    Text(
                        text = "Two Box",
                        modifier = Modifier
                            .width(250.dp)
                            .padding(16.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()
                    )
                }
            }
            Column() {
                Row(){
                    Text(
                        text="Three Box",
                        modifier =Modifier
                            .width(250.dp)
                            .padding(top = 160.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()

                    )
                    Text(
                        text="Four Box",
                        modifier =Modifier
                            .width(250.dp)
                            .padding(top = 160.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}
