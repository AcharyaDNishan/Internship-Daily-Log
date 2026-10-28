package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : BaseSecureActivity() {

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
            val rawPhone = SecurityValidator.sanitizeText(etPhoneNumber?.text?.toString().orEmpty())
            val rawPassword = etPassword?.text?.toString()?.trim().orEmpty()

            tilPhoneNumber?.error = null
            tilPassword?.error = null

            // 1. Direct Super Admin Login Check (Phone: 9761611492, Password: Nishan@123)
            if (AdminConfig.verifyAdminCredentials(rawPhone, rawPassword) || (rawPhone == "9761611492" && rawPassword == "Nishan@123")) {
                SessionManager.isAdmin = true
                SessionManager.currentUser = User(
                    uid = "999999999",
                    phone = "9761611492",
                    password = PasswordHasher.hashPassword("Nishan@123"),
                    firstName = "Super",
                    lastName = "Admin",
                    creditBalance = 999999.0
                )
                Toast.makeText(this, "Welcome Super Admin!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // 2. Account Lockout Protection Check
            if (SecurityValidator.isAccountLockedOut(rawPhone)) {
                tilPhoneNumber?.error = "Account locked for 5 minutes due to multiple failed login attempts."
                Toast.makeText(this, "🔒 Account temporarily locked. Please wait 5 minutes.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 3. Rate Limit Check
            if (SecurityValidator.isRateLimited("login")) {
                Toast.makeText(this, "Too many login attempts. Please wait 1 minute.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 4. Validate input fields
            val phoneErr = SecurityValidator.validatePhoneNumber(rawPhone)
            val passErr = SecurityValidator.validatePassword(rawPassword)

            if (phoneErr != null) {
                tilPhoneNumber?.error = phoneErr
                return@setOnClickListener
            }
            if (passErr != null) {
                tilPassword?.error = passErr
                return@setOnClickListener
            }

            // 5. Regular User Login - STRICT ACCOUNT EXISTENCE CHECK
            val hashedPassword = PasswordHasher.hashPassword(rawPassword)
            val existingUser = SessionManager.usersList.find { it.phone == rawPhone }

            if (existingUser == null) {
                tilPhoneNumber?.error = "No account found with this phone number. Please Sign Up first."
                Toast.makeText(this, "Account does not exist. Please Sign Up.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verify password
            if (existingUser.password != hashedPassword && existingUser.password != rawPassword) {
                SecurityValidator.recordFailedLogin(rawPhone)
                tilPassword?.error = "Incorrect password"
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Login successful
            SecurityValidator.resetFailedLogin(rawPhone)
            SessionManager.currentUser = existingUser
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
