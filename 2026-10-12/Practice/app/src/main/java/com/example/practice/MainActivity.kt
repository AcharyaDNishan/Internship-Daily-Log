package com.example.practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etFirstName = findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = findViewById<TextInputEditText>(R.id.etLastName)
        val etDob = findViewById<TextInputEditText>(R.id.etDob)
        val etCountry = findViewById<TextInputEditText>(R.id.etCountry)
        val password = findViewById<TextInputEditText>(R.id.pw)
        val btnApply = findViewById<Button>(R.id.button)
        val login = findViewById<TextView>(R.id.login)

        btnApply.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val dob = etDob.text.toString()
            val country = etCountry.text.toString()
            val pw = password.text.toString()
            val data = listOf(firstName, lastName, dob, country, pw).joinToString(",")
            try {
                openFileOutput("Data.txt", MODE_PRIVATE).bufferedWriter().use { writer ->
                    writer.write(data)
                    writer.newLine()
                }
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
