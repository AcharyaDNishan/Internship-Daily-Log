package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

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
            val normalizedPhone = SecurityValidator.normalizePhone(rawPhone)

            tilPhoneNumber?.error = null
            tilPassword?.error = null

            // 1. Direct Super Admin Login Check (Phone: 9761611492, Password: Nishan@123)
            if (normalizedPhone == "9761611492" && rawPassword == "Nishan@123" || AdminConfig.verifyAdminCredentials(rawPhone, rawPassword)) {
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
            if (SecurityValidator.isAccountLockedOut(normalizedPhone)) {
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

            // 5. Regular User Login - Check Supabase DB + Local Session with normalized phone
            lifecycleScope.launch {
                var existingUser = SessionManager.usersList.find {
                    SecurityValidator.normalizePhone(it.phone) == normalizedPhone
                }

                if (existingUser == null) {
                    try {
                        val dbUsers = SupabaseClient.client.from("users").select().decodeList<User>()
                        existingUser = dbUsers.find { SecurityValidator.normalizePhone(it.phone) == normalizedPhone }
                        if (existingUser != null && !SessionManager.usersList.contains(existingUser)) {
                            SessionManager.usersList.add(existingUser)
                        }
                    } catch (e: Exception) {
                        // Fallback
                    }
                }

                if (existingUser == null) {
                    tilPhoneNumber?.error = "No account found for $normalizedPhone. Please Sign Up first."
                    Toast.makeText(this@LoginActivity, "Account does not exist. Please Sign Up.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val hashedPassword = PasswordHasher.hashPassword(rawPassword)
                if (existingUser.password != hashedPassword && existingUser.password != rawPassword) {
                    SecurityValidator.recordFailedLogin(normalizedPhone)
                    tilPassword?.error = "Incorrect password"
                    Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Login Successful
                SecurityValidator.resetFailedLogin(normalizedPhone)
                SessionManager.currentUser = existingUser
                SessionManager.isAdmin = false
                Toast.makeText(this@LoginActivity, "Welcome back, ${existingUser.firstName}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                finish()
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
