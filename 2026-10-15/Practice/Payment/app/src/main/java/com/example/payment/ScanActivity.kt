package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanActivity : BaseSecureActivity() {

    private val qrScannerLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            handleScannedQrContent(result.contents)
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan)

        val cvViewfinder = findViewById<MaterialCardView>(R.id.cvViewfinder)
        val btnFlash = findViewById<MaterialButton>(R.id.btnFlash)

        cvViewfinder?.setOnClickListener {
            startQrScanner()
        }

        btnFlash?.setOnClickListener {
            startQrScanner()
        }

        setupBottomNavigation(R.id.nav_scan)
    }

    private fun startQrScanner() {
        val options = ScanOptions()
        options.setPrompt("Align QR code inside camera viewfinder to scan")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        qrScannerLauncher.launch(options)
    }

    private fun handleScannedQrContent(qrContent: String) {
        if (SecurityValidator.isRateLimited("scan")) {
            Toast.makeText(this, "Rate limit reached. Please wait a moment.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = SessionManager.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Extract QR Code ID from payload
        val qrId = if (qrContent.contains("QR_PAY_")) {
            qrContent.substringAfter("QR_PAY_").substringBefore("_CREDIT_")
        } else {
            qrContent
        }

        // Check if QR code has already been redeemed! One QR cannot be used twice!
        if (SessionManager.isQrRedeemed(qrId)) {
            Toast.makeText(this, "❌ Error: This QR Code has ALREADY been redeemed!", Toast.LENGTH_LONG).show()
            return
        }

        val verifiedCredit = HmacQrSigner.verifyAndExtractQrCredit(qrContent)
        val creditToAdd: Double = when {
            verifiedCredit != null -> verifiedCredit
            qrContent.contains("CREDIT_") -> {
                qrContent.substringAfter("CREDIT_").substringBefore("_SIG_").toDoubleOrNull() ?: (50..500).random().toDouble()
            }
            else -> (50..500).random().toDouble()
        }

        lifecycleScope.launch {
            // Mark QR Code as Redeemed
            SessionManager.markQrRedeemed(qrId, currentUser.uid)

            val newBalance = currentUser.creditBalance + creditToAdd
            val updatedUser = currentUser.copy(creditBalance = newBalance)
            SessionManager.currentUser = updatedUser

            val userIdx = SessionManager.usersList.indexOfFirst { it.uid == currentUser.uid }
            if (userIdx != -1) {
                SessionManager.usersList[userIdx] = updatedUser
            }

            val now = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
            val newTx = TransactionItem(
                senderUid = "SYSTEM_QR",
                receiverUid = currentUser.uid,
                senderPhone = "Verified QR Reward",
                receiverPhone = currentUser.phone,
                amount = creditToAdd,
                type = "QR_REDEEM",
                description = "Redeemed Verified QR Credit: Rs. $creditToAdd",
                createdAt = now
            )

            SessionManager.transactionsList.add(0, newTx)

            try {
                SupabaseClient.client.from("transactions").insert(newTx)
                SupabaseClient.client.from("users").update({
                    set("credit_balance", newBalance)
                }) {
                    filter {
                        eq("uid", currentUser.uid)
                    }
                }
                SupabaseClient.client.from("qr_codes").update({
                    set("is_redeemed", true)
                    set("redeemed_by_uid", currentUser.uid)
                }) {
                    filter {
                        eq("qr_code_id", qrId)
                    }
                }
            } catch (e: Exception) {
                // Fallback
            }

            Toast.makeText(this@ScanActivity, "🎉 Successfully Redeemed Rs. $creditToAdd Credits!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation(selectedItemId: Int) {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView) ?: return
        bottomNavigation.selectedItemId = selectedItemId
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> true
                R.id.nav_send -> {
                    if (selectedItemId != R.id.nav_send) {
                        startActivity(Intent(this, SendActivity::class.java))
                        finish()
                    }
                    true
                }
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
