package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.github.jan_tennert.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SendActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.send)

        val etRecipient = findViewById<TextInputEditText>(R.id.etRecipient)
        val btnSend = findViewById<MaterialButton>(R.id.btnSend)

        btnSend?.setOnClickListener {
            val input = etRecipient?.text?.toString()?.trim().orEmpty()
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter recipient phone number or UID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = SessionManager.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@setOnClickListener
            }

            val amount = 100.0 // Default transaction amount
            if (currentUser.creditBalance < amount) {
                Toast.makeText(this, "Insufficient credit balance!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val updatedSender = currentUser.copy(creditBalance = currentUser.creditBalance - amount)
                SessionManager.currentUser = updatedSender

                // Update local list user
                val localUserIdx = SessionManager.usersList.indexOfFirst { it.uid == currentUser.uid }
                if (localUserIdx != -1) {
                    SessionManager.usersList[localUserIdx] = updatedSender
                }

                // Add to recipient if found
                val recipientUser = SessionManager.usersList.find { it.phone == input || it.uid == input }
                if (recipientUser != null) {
                    val recipientIdx = SessionManager.usersList.indexOf(recipientUser)
                    SessionManager.usersList[recipientIdx] = recipientUser.copy(creditBalance = recipientUser.creditBalance + amount)
                }

                val now = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                val newTx = TransactionItem(
                    senderUid = currentUser.uid,
                    receiverUid = input,
                    senderPhone = currentUser.phone,
                    receiverPhone = input,
                    amount = amount,
                    type = "SENT",
                    description = "Payment to $input",
                    createdAt = now
                )

                SessionManager.transactionsList.add(0, newTx)

                try {
                    SupabaseClient.client.from("transactions").insert(newTx)
                } catch (e: Exception) {
                    // Ignored if network/table error
                }

                Toast.makeText(this@SendActivity, "₹$amount Sent Successfully to $input!", Toast.LENGTH_LONG).show()
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
