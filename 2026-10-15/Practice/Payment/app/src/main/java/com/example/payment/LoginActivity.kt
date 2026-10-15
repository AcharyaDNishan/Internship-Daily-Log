package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

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
            val password = etPassword?.text?.toString().orEmpty()

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

            // Secure Hashed Admin Check
            if (AdminConfig.verifyAdminCredentials(phone, password)) {
                SessionManager.isAdmin = true
                SessionManager.currentUser = User(
                    uid = "999999999",
                    phone = "9761611492",
                    password = "Admin",
                    firstName = "Admin",
                    lastName = "Nishan",
                    creditBalance = 999999.0
                )
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // Regular User Login
            lifecycleScope.launch {
                val localUser = SessionManager.usersList.find { it.phone == phone && it.password == password }
                if (localUser != null) {
                    SessionManager.currentUser = localUser
                    SessionManager.isAdmin = false
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                    finish()
                } else {
                    tilPassword?.error = "Invalid phone number or password"
                    Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvForgotPassword?.setOnClickListener {
            Toast.makeText(this, "Reset link sent to registered phone number", Toast.LENGTH_SHORT).show()
        }

        tvSignUp?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
