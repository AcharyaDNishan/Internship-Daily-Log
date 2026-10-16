package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val cvCreateQr = findViewById<MaterialCardView>(R.id.cvCreateQr)
        val cvViewUsers = findViewById<MaterialCardView>(R.id.cvViewUsers)
        val btnAdminLogout = findViewById<MaterialButton>(R.id.btnAdminLogout)

        cvCreateQr?.setOnClickListener {
            startActivity(Intent(this, AdminQrCreateActivity::class.java))
        }

        cvViewUsers?.setOnClickListener {
            startActivity(Intent(this, AdminViewUsersActivity::class.java))
        }

        btnAdminLogout?.setOnClickListener {
            SessionManager.currentUser = null
            SessionManager.isAdmin = false
            Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
