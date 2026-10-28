package com.example.iqtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Secondary : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val r = painterResource(R.drawable.ic_task_completed)
                Image(
                    painter = r,
                    contentDescription = "Task Completed",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            val intent = Intent(context, Third::class.java)
                            context.startActivity(intent)
                            print("Clicked.")
                        },
                )
                Text(
                    text = "All Tasks Completed!",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp),
                )
                Text(
                    text = "Nice Work!",
                    fontSize = 16.sp,
                )
            }
        }
    }
}
