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
            val firstName = etFirstName?.text?.toString()?.trim().orEmpty()
            val lastName = etLastName?.text?.toString()?.trim().orEmpty()
            val phone = etPhoneNumber?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString()?.trim().orEmpty()

            tilFirstName?.error = null
            tilLastName?.error = null
            tilPhoneNumber?.error = null
            tilPassword?.error = null

            when {
                firstName.isEmpty() -> {
                    tilFirstName?.error = "Please enter first name"
                    return@setOnClickListener
                }
                lastName.isEmpty() -> {
                    tilLastName?.error = "Please enter last name"
                    return@setOnClickListener
                }
                phone.isEmpty() -> {
                    tilPhoneNumber?.error = "Please enter phone number"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    tilPassword?.error = "Please enter password"
                    return@setOnClickListener
                }
            }

            val newUid = SessionManager.generateUnique9DigitUid()
            val newUser = User(
                uid = newUid,
                phone = phone,
                password = password,
                firstName = firstName,
                lastName = lastName,
                creditBalance = 100.0 // Welcome bonus credit
            )

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.from("users").insert(newUser)
                } catch (e: Exception) {
                    // Ignored if network/table error, local fallback handles it
                }

                SessionManager.usersList.add(newUser)
                SessionManager.currentUser = newUser
                SessionManager.isAdmin = false

                Toast.makeText(this@SignInActivity, "Sign Up Successful! Welcome UID: $newUid", Toast.LENGTH_LONG).show()
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
