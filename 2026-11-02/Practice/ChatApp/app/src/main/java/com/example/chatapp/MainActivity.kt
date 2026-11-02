
package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var nickname by rememberSaveable {
                mutableStateOf("")
            }
            var loggedIn by rememberSaveable {
                mutableStateOf(false)
            }
            if (loggedIn) {
                val intent= Intent(this, Login::class.java)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
            } else {
                LoginScreen(
                    nickname = nickname,
                    onNicknameChange = { nickname = it },
                    onLogin = { loggedIn = true }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("Nickname") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogin,
            enabled = nickname.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}
