package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AdminActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val cvCreateQr = findViewById<MaterialCardView>(R.id.cvCreateQr)
        val cvViewUsers = findViewById<MaterialCardView>(R.id.cvViewUsers)
        val btnAdminSend = findViewById<MaterialButton>(R.id.btnAdminSend)
        val btnAdminScan = findViewById<MaterialButton>(R.id.btnAdminScan)
        val btnAdminLogout = findViewById<MaterialButton>(R.id.btnAdminLogout)

        cvCreateQr?.setOnClickListener {
            startActivity(Intent(this, AdminQrCreateActivity::class.java))
        }

        cvViewUsers?.setOnClickListener {
            startActivity(Intent(this, AdminViewUsersActivity::class.java))
        }

        btnAdminSend?.setOnClickListener {
            startActivity(Intent(this, SendActivity::class.java))
        }

        btnAdminScan?.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }

        btnAdminLogout?.setOnClickListener {
            SessionManager.currentUser = null
            SessionManager.isAdmin = false
            Toast.makeText(this, "Admin Logged Out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> {
                    startActivity(Intent(this, ScanActivity::class.java))
                    true
                }
                R.id.nav_send -> {
                    startActivity(Intent(this, SendActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
