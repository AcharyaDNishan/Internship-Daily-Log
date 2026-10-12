package com.example.practice

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val etFirstName = findViewById<TextInputEditText>(R.id.fdlog)
        val etPassword = findViewById<TextInputEditText>(R.id.pwlog)
        val btnApply = findViewById<Button>(R.id.buttonlog)
        val signin = findViewById<TextView>(R.id.signin)
        signin.setOnClickListener {
            finish()
        }
    }
}
