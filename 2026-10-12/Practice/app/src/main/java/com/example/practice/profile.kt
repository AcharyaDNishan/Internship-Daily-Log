package com.example.practice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class profile: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val etFirstName = findViewById<EditText>(R.id.fn)
        val etLastName = findViewById<EditText>(R.id.ln)
        val etDob = findViewById<EditText>(R.id.dob)
        val etCountry = findViewById<EditText>(R.id.con)
        val uid = intent.getStringExtra("uid")
        try {
            val f = File(filesDir, "Data.txt")
            if (f.exists()) {
                val lines = f.readLines()
                lines.forEach { row ->
                    val columns = row.split(",")
                    if (columns.size >= 6) {
                        val savedUID = columns[5].trim()
                        if (uid == savedUID) {
                            val fn:String = columns[0]
                            val ln: String = columns[1]
                            val db = columns[2]
                            val cn = columns[3]
                            etFirstName.setText(fn)
                            etLastName.setText(ln)
                            etDob.setText(db)
                            etCountry.setText(cn)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}