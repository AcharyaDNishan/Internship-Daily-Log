package com.example.practice

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class profile: ComponentActivity() {
    fun onCreate(savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val etFirstName = findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = findViewById<TextInputEditText>(R.id.etLastName)
        val etDob = findViewById<TextInputEditText>(R.id.etDob)
        val etCountry = findViewById<TextInputEditText>(R.id.etCountry)
        val uid = intent.getStringExtra("uid")
        try {
            val f = File(filesDir, "Data.txt")
            if (f.exists()) {
                val lines = f.readLines()
                lines.forEach { row ->
                    val columns = row.split(",")
                    if (columns.size >= 5) {
                        val savedUID = columns[5].trim()
                        if(uid==savedUID){
                            etFirstName.text=columns[0]
                            etLastName.text=columns[1]
                            etDob.text=columns[2]
                            etCountry.text=columns[3]
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}