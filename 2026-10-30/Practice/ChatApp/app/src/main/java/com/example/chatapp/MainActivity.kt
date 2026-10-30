
package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val file = remember {
                File(context.filesDir, "Chat.txt")
            }
            val messages = remember {
                mutableStateListOf<String>()
            }
            var message by rememberSaveable {
                mutableStateOf("")
            }
            val scope = rememberCoroutineScope()

            // Read messages when the screen opens
            LaunchedEffect(Unit) {
                val savedMessages = withContext(Dispatchers.IO) {
                    if (file.exists()) file.readLines()
                    else emptyList()
                }

                messages.addAll(savedMessages)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(messages) { msg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Message") },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            val newMessage = message.trim()

                            if (newMessage.isNotEmpty()) {

                                scope.launch {

                                    withContext(Dispatchers.IO) {
                                        file.appendText("$newMessage\n")
                                    }

                                    messages.add(newMessage)
                                    message = ""
                                }
                            }
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}
