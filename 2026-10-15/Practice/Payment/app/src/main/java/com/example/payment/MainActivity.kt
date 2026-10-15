package com.example.payment

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoginPage()
    }

    private fun showLoginPage() {
        setContentView(R.layout.login_page)
        setupBottomNavigation(R.id.nav_profile)

        val tilPhoneNumber = findViewById<TextInputLayout>(R.id.tilPhoneNumber)
        val etPhoneNumber = findViewById<TextInputEditText>(R.id.etPhoneNumber)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin?.setOnClickListener {
            val phone = etPhoneNumber?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()

            tilPhoneNumber?.error = null
            tilPassword?.error = null

            when {
                phone.isEmpty() -> tilPhoneNumber?.error = "Please enter your phone number"
                password.isEmpty() -> tilPassword?.error = getString(R.string.please_enter_password)
                else -> {
                    Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                    showProfilePage()
                }
            }
        }

        tvForgotPassword?.setOnClickListener {
            Toast.makeText(this, getString(R.string.forgot_password), Toast.LENGTH_SHORT).show()
        }

        tvSignUp?.setOnClickListener {
            showSignInPage()
        }
    }

    private fun showSignInPage() {
        setContentView(R.layout.signin_page)
        setupBottomNavigation(R.id.nav_profile)

        val tilFirstName = findViewById<TextInputLayout>(R.id.tilFirstName)
        val etFirstName = findViewById<TextInputEditText>(R.id.etFirstName)
        val tilLastName = findViewById<TextInputLayout>(R.id.tilLastName)
        val etLastName = findViewById<TextInputEditText>(R.id.etLastName)
        val tilPhoneNumber = findViewById<TextInputLayout>(R.id.tilPhoneNumber)
        val etPhoneNumber = findViewById<TextInputEditText>(R.id.etPhoneNumber)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnSignIn = findViewById<MaterialButton>(R.id.btnSignIn)
        val tvAlreadyHaveAccount = findViewById<TextView>(R.id.tvAlreadyHaveAccount)

        btnSignIn?.setOnClickListener {
            val firstName = etFirstName?.text?.toString()?.trim().orEmpty()
            val lastName = etLastName?.text?.toString()?.trim().orEmpty()
            val phone = etPhoneNumber?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()

            tilFirstName?.error = null
            tilLastName?.error = null
            tilPhoneNumber?.error = null
            tilPassword?.error = null

            when {
                firstName.isEmpty() -> tilFirstName?.error = "Please enter first name"
                lastName.isEmpty() -> tilLastName?.error = "Please enter last name"
                phone.isEmpty() -> tilPhoneNumber?.error = "Please enter phone number"
                password.isEmpty() -> tilPassword?.error = "Please enter password"
                else -> {
                    Toast.makeText(this, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                    showProfilePage()
                }
            }
        }

        tvAlreadyHaveAccount?.setOnClickListener {
            showLoginPage()
        }
    }

    private fun showSendPage() {
        setContentView(R.layout.send)
        setupBottomNavigation(R.id.nav_send)

        val etRecipient = findViewById<TextInputEditText>(R.id.etRecipient)
        val btnSend = findViewById<MaterialButton>(R.id.btnSend)

        btnSend?.setOnClickListener {
            val recipient = etRecipient?.text?.toString()?.trim().orEmpty()
            if (recipient.isEmpty()) {
                Toast.makeText(this, "Please enter mobile number or UPI ID", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment sent to $recipient", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showScanPage() {
        setContentView(R.layout.scan)
        setupBottomNavigation(R.id.nav_scan)

        val btnFlash = findViewById<MaterialButton>(R.id.btnFlash)
        val btnGallery = findViewById<MaterialButton>(R.id.btnGallery)

        btnFlash?.setOnClickListener {
            Toast.makeText(this, "Flashlight toggled", Toast.LENGTH_SHORT).show()
        }

        btnGallery?.setOnClickListener {
            Toast.makeText(this, "Open Gallery for QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHistoryPage() {
        setContentView(R.layout.history)
        setupBottomNavigation(R.id.nav_history)
    }

    private fun showProfilePage() {
        setContentView(R.layout.profil_view)
        setupBottomNavigation(R.id.nav_profile)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            showLoginPage()
        }
    }

    private fun setupBottomNavigation(selectedItemId: Int) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
        bottomNavigation.setOnItemSelectedListener(null)
        bottomNavigation.selectedItemId = selectedItemId
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> {
                    if (selectedItemId != R.id.nav_scan) showScanPage()
                    true
                }
                R.id.nav_send -> {
                    if (selectedItemId != R.id.nav_send) showSendPage()
                    true
                }
                R.id.nav_history -> {
                    if (selectedItemId != R.id.nav_history) showHistoryPage()
                    true
                }
                R.id.nav_profile -> {
                    if (selectedItemId != R.id.nav_profile) showProfilePage()
                    true
                }
                else -> false
            }
        }
    }
}
