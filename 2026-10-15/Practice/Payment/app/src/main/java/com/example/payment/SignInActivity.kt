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
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class SignInActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_page)

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
            if (SecurityValidator.isRateLimited("signin")) {
                Toast.makeText(this, "Too many sign-up attempts. Please wait 1 minute.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val firstName = SecurityValidator.sanitizeText(etFirstName?.text?.toString().orEmpty())
            val lastName = SecurityValidator.sanitizeText(etLastName?.text?.toString().orEmpty())
            val phone = SecurityValidator.sanitizeText(etPhoneNumber?.text?.toString().orEmpty())
            val password = etPassword?.text?.toString().orEmpty()

            tilFirstName?.error = null
            tilLastName?.error = null
            tilPhoneNumber?.error = null
            tilPassword?.error = null

            val fnErr = SecurityValidator.validateName(firstName, "First Name")
            val lnErr = SecurityValidator.validateName(lastName, "Last Name")
            val phoneErr = SecurityValidator.validatePhoneNumber(phone)
            val passErr = SecurityValidator.validatePassword(password)

            if (fnErr != null) {
                tilFirstName?.error = fnErr
                return@setOnClickListener
            }
            if (lnErr != null) {
                tilLastName?.error = lnErr
                return@setOnClickListener
            }
            if (phoneErr != null) {
                tilPhoneNumber?.error = phoneErr
                return@setOnClickListener
            }
            if (passErr != null) {
                tilPassword?.error = passErr
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val newUid = SessionManager.generateUnique9DigitUid()
                val newUser = User(
                    uid = newUid,
                    phone = phone,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    creditBalance = 0.0 // Account starts at zero credit
                )

                try {
                    SupabaseClient.client.from("users").insert(newUser)
                } catch (e: Exception) {
                    // Fallback to local session
                }

                SessionManager.usersList.add(newUser)
                SessionManager.currentUser = newUser
                SessionManager.isAdmin = false

                Toast.makeText(this@SignInActivity, "Account Created! Unique UID: $newUid | Balance: Rs. 0.00", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@SignInActivity, ProfileActivity::class.java))
                finish()
            }
        }

        tvAlreadyHaveAccount?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
