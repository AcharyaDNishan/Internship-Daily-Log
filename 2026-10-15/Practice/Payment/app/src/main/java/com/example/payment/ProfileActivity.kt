package com.example.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ProfileActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil_view)

        val user = SessionManager.currentUser

        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileContact = findViewById<TextView>(R.id.tvProfileContact)
        val tvUpiId = findViewById<TextView>(R.id.tvUpiId)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        if (user != null) {
            tvProfileName?.text = "${user.firstName} ${user.lastName}".trim()
            tvProfileContact?.text = "Phone: ${user.phone}"
            tvUpiId?.text = "UID: ${user.uid} | Account Credit: Rs. ${user.creditBalance}"

            tvProfileContact?.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${user.phone}"))
                try {
                    startActivity(callIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Calling ${user.phone}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            tvProfileName?.text = "Guest User"
            tvProfileContact?.text = "Phone: 9761611492"
            tvUpiId?.text = "Account Credit: Rs. 0.00"

            tvProfileContact?.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9761611492"))
                try {
                    startActivity(callIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Calling 9761611492", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnLogout?.setOnClickListener {
            SessionManager.currentUser = null
            SessionManager.isAdmin = false
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setupBottomNavigation(R.id.nav_profile)
    }

    private fun setupBottomNavigation(selectedItemId: Int) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
        bottomNavigation.selectedItemId = selectedItemId
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> {
                    if (selectedItemId != R.id.nav_scan) {
                        startActivity(Intent(this, ScanActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_send -> {
                    if (selectedItemId != R.id.nav_send) {
                        startActivity(Intent(this, SendActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_history -> {
                    if (selectedItemId != R.id.nav_history) {
                        startActivity(Intent(this, HistoryActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}
