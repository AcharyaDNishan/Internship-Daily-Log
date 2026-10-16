package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SendActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send)

        val tilRecipient = findViewById<TextInputLayout>(R.id.tilRecipient)
        val etRecipient = findViewById<TextInputEditText>(R.id.etRecipient)
        val btnSend = findViewById<MaterialButton>(R.id.btnSend)

        btnSend?.setOnClickListener {
            if (SecurityValidator.isRateLimited("send")) {
                Toast.makeText(this, "Rate limit exceeded. Please wait a moment.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipientInput = SecurityValidator.sanitizeText(etRecipient?.text?.toString().orEmpty())
            tilRecipient?.error = null

            if (recipientInput.isEmpty()) {
                tilRecipient?.error = "Please enter mobile number or UID"
                return@setOnClickListener
            }

            val currentUser = SessionManager.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@setOnClickListener
            }

            val amount = 100.0
            if (currentUser.creditBalance < amount) {
                tilRecipient?.error = "Insufficient credit balance (Rs. ${currentUser.creditBalance} available)"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val newSenderBalance = currentUser.creditBalance - amount
                val updatedSender = currentUser.copy(creditBalance = newSenderBalance)
                SessionManager.currentUser = updatedSender

                val senderIdx = SessionManager.usersList.indexOfFirst { it.uid == currentUser.uid }
                if (senderIdx != -1) {
                    SessionManager.usersList[senderIdx] = updatedSender
                }

                val now = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                val newTx = TransactionItem(
                    senderUid = currentUser.uid,
                    receiverUid = recipientInput,
                    senderPhone = currentUser.phone,
                    receiverPhone = recipientInput,
                    amount = amount,
                    type = "SENT",
                    description = "Sent Rs. $amount to $recipientInput",
                    createdAt = now
                )

                SessionManager.transactionsList.add(0, newTx)

                try {
                    SupabaseClient.client.from("transactions").insert(newTx)
                    SupabaseClient.client.from("users").update({
                        set("credit_balance", newSenderBalance)
                    }) {
                        filter {
                            eq("uid", currentUser.uid)
                        }
                    }
                } catch (e: Exception) {
                    // Fallback
                }

                Toast.makeText(this@SendActivity, "Rs. $amount Sent Successfully to $recipientInput!", Toast.LENGTH_LONG).show()
                etRecipient?.text?.clear()
            }
        }

        setupBottomNavigation(R.id.nav_send)
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
                R.id.nav_send -> true
                R.id.nav_history -> {
                    if (selectedItemId != R.id.nav_history) {
                        startActivity(Intent(this, HistoryActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (selectedItemId != R.id.nav_profile) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
