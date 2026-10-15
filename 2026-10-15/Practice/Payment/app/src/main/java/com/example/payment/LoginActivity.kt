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
import io.github.jan_tennert.supabase.postgrest.from
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
            val phone = etPhoneNumber?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()

            tilPhoneNumber?.error = null
            tilPassword?.error = null

            if (phone.isEmpty()) {
                tilPhoneNumber?.error = "Please enter phone number"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tilPassword?.error = getString(R.string.please_enter_password)
                return@setOnClickListener
            }

            // Check Admin Login
            if (phone == "9761611492" && password == "Nishan@123") {
                SessionManager.isAdmin = true
                SessionManager.currentUser = User(
                    uid = "999999999",
                    phone = "9761611492",
                    password = "Nishan@123",
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
                try {
                    val users = SupabaseClient.client.from("users")
                        .select {
                            filter {
                                eq("phone", phone)
                                eq("password", password)
                            }
                        }.decodeList<User>()

                    val loggedInUser = users.firstOrNull() ?: SessionManager.usersList.find { it.phone == phone && it.password == password }

                    if (loggedInUser != null) {
                        SessionManager.currentUser = loggedInUser
                        SessionManager.isAdmin = false
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Phone Number or Password", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    val localUser = SessionManager.usersList.find { it.phone == phone && it.password == password }
                    if (localUser != null) {
                        SessionManager.currentUser = localUser
                        SessionManager.isAdmin = false
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvForgotPassword?.setOnClickListener {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        }

        tvSignUp?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
