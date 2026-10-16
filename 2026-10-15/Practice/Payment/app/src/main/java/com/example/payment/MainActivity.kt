package com.example.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            SessionManager.isAdmin -> {
                startActivity(Intent(this, AdminActivity::class.java))
            }
            SessionManager.currentUser != null -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }
}
