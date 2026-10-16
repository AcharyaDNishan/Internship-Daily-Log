package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val tilPhoneNumber = findViewById<TextInputLayout>(R.id.tilPhoneNumber)
        val etPhoneNumber = findViewById<TextInputEditText>(R.id.etPhoneNumber)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin?.setOnClickListener {
            if (SecurityValidator.isRateLimited("login")) {
                Toast.makeText(this, "Too many login attempts. Please wait 1 minute.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val phone = SecurityValidator.sanitizeText(etPhoneNumber?.text?.toString().orEmpty())
            val password = etPassword?.text?.toString().orEmpty().trim()

            tilPhoneNumber?.error = null
            tilPassword?.error = null

            val phoneErr = SecurityValidator.validatePhoneNumber(phone)
            val passErr = SecurityValidator.validatePassword(password)

            if (phoneErr != null) {
                tilPhoneNumber?.error = phoneErr
                return@setOnClickListener
            }
            if (passErr != null) {
                tilPassword?.error = passErr
                return@setOnClickListener
            }

            // Direct Admin Login Check
            if (AdminConfig.verifyAdminCredentials(phone, password)) {
                SessionManager.isAdmin = true
                SessionManager.currentUser = User(
                    uid = "999999999",
                    phone = "9761611492",
                    password = "Nishan@123",
                    firstName = "Super",
                    lastName = "Admin",
                    creditBalance = 999999.0
                )
                Toast.makeText(this, "Welcome Super Admin!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // Regular User Login Check
            var localUser = SessionManager.usersList.find { it.phone == phone }
            if (localUser != null) {
                if (localUser.password != password) {
                    tilPassword?.error = "Incorrect password"
                    return@setOnClickListener
                }
            } else {
                val newUid = SessionManager.generateUnique9DigitUid()
                localUser = User(
                    uid = newUid,
                    phone = phone,
                    password = password,
                    firstName = "User",
                    lastName = phone.takeLast(4),
                    creditBalance = 0.0 // Account starts at zero credit
                )
                SessionManager.usersList.add(localUser)
            }

            SessionManager.currentUser = localUser
            SessionManager.isAdmin = false
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        tvForgotPassword?.setOnClickListener {
            Toast.makeText(this, "Reset link sent to registered phone number", Toast.LENGTH_SHORT).show()
        }

        tvSignUp?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
