package com.example.practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import kotlin.random.Random

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
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val dob = etDob.text.toString().trim()
            val country = etCountry.text.toString().trim()
            val pw = password.text.toString().trim()

            if (firstName.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "Please enter First Name and Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rf = File(filesDir, "Data.txt")
            var unique = false
            var uid = Random.nextInt(1000, 9999)
            while (!unique) {
                unique = true
                if (rf.exists()) {
                    val lines = rf.readLines()
                    for (row in lines) {
                        val columns = row.split(",")
                        if (columns.size >= 6) {
                            val savedUid = columns[5].trim()
                            if (savedUid == uid.toString()) {
                                unique = false
                                uid = Random.nextInt(1000, 9999)
                                break
                            }
                        }
                    }
                }
            }
            val data = listOf(firstName, lastName, dob, country, pw, uid).joinToString(",")
            val f = File(filesDir, "Data.txt")
            f.appendText(data + "\n")
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
        }
        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
