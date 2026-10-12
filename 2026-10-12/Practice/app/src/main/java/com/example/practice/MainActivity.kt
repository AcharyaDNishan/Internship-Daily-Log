package com.example.practice

import android.os.Bundle
import android.widget.Button
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
        val btnApply = findViewById<Button>(R.id.button)

        // 2. Set click listener on the button to read the text into variables
        btnApply.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val dob = etDob.text.toString()
            val country = etCountry.text.toString()

            // 3. Use the variables (e.g., show a Toast message)
            val message = "Name: $firstName $lastName, DOB: $dob, Country: $country"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
