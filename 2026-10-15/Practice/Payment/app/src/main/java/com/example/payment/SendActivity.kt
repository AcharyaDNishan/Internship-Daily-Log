package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

class SendActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send)

        val tilRecipient = findViewById<TextInputLayout>(R.id.tilRecipient)
        val etRecipient = findViewById<TextInputEditText>(R.id.etRecipient)
        val tilAmount = findViewById<TextInputLayout>(R.id.tilAmount)
        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val btnSend = findViewById<MaterialButton>(R.id.btnSend)

        btnSend?.setOnClickListener {
            if (SecurityValidator.isRateLimited("send")) {
                Toast.makeText(this, "Rate limit exceeded. Please wait a moment.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipientPhone = SecurityValidator.sanitizeText(etRecipient?.text?.toString().orEmpty())
            val amountStr = etAmount?.text?.toString()?.trim().orEmpty()
            val sendAmount = amountStr.toDoubleOrNull() ?: 0.0

            tilRecipient?.error = null
            tilAmount?.error = null

            val phoneErr = SecurityValidator.validatePhoneNumber(recipientPhone)
            if (phoneErr != null) {
                tilRecipient?.error = phoneErr
                return@setOnClickListener
            }

            if (sendAmount <= 0) {
                tilAmount?.error = "Please enter a valid amount greater than 0"
                return@setOnClickListener
            }

            val currentUser = SessionManager.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@setOnClickListener
            }

            if (currentUser.phone == recipientPhone) {
                tilRecipient?.error = "Cannot send money to your own phone number"
                return@setOnClickListener
            }

            if (currentUser.creditBalance < sendAmount) {
                tilAmount?.error = "Insufficient credit balance (Rs. ${currentUser.creditBalance} available)"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val newSenderBalance = currentUser.creditBalance - sendAmount
                val updatedSender = currentUser.copy(creditBalance = newSenderBalance)
                SessionManager.currentUser = updatedSender

                val senderIdx = SessionManager.usersList.indexOfFirst { it.uid == currentUser.uid }
                if (senderIdx != -1) {
                    SessionManager.usersList[senderIdx] = updatedSender
                }

                // Add to recipient balance if registered
                val recipientUser = SessionManager.usersList.find { it.phone == recipientPhone || it.uid == recipientPhone }
                if (recipientUser != null) {
                    val recipientIdx = SessionManager.usersList.indexOf(recipientUser)
                    SessionManager.usersList[recipientIdx] = recipientUser.copy(creditBalance = recipientUser.creditBalance + sendAmount)
                }

                val now = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                val newTx = TransactionItem(
                    senderUid = currentUser.uid,
                    receiverUid = recipientPhone,
                    senderPhone = currentUser.phone,
                    receiverPhone = recipientPhone,
                    amount = sendAmount,
                    type = "SENT",
                    description = "Sent Rs. $sendAmount to $recipientPhone",
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

                Toast.makeText(this@SendActivity, "Rs. $sendAmount Sent Successfully to $recipientPhone!", Toast.LENGTH_LONG).show()
                etRecipient?.text?.clear()
                etAmount?.text?.clear()
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
