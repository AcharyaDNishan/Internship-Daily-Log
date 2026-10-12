package com.example.practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        btnApply.setOnClickListener {
            val inputUser = etFirstName.text.toString().trim()
            val inputPass = etPassword.text.toString().trim()
            var loggedIn = false
            try {
                openFileInput("Data.txt").bufferedReader().useLines { lines ->
                    lines.forEach { row ->
                        val columns = row.split(",")
                        if (columns.size >= 5) {
                            val savedUser = columns[0].trim()
                            val savedPass = columns[4].trim()
                            if (inputUser == savedUser && inputPass == savedPass) {
                                loggedIn = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            if (loggedIn) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, loggedin::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
