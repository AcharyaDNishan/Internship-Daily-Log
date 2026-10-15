package com.example.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import io.github.jan_tennert.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanActivity : ComponentActivity() {

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
        val btnGallery = findViewById<MaterialButton>(R.id.btnGallery)

        cvViewfinder?.setOnClickListener {
            startQrScanner()
        }

        btnFlash?.setOnClickListener {
            startQrScanner()
        }

        btnGallery?.setOnClickListener {
            // Simulate random QR redeem for quick testing or gallery QR
            val randomCredit = (50..500).random().toDouble()
            handleScannedQrContent("QR_PAY_${System.currentTimeMillis()}_CREDIT_$randomCredit")
        }

        setupBottomNavigation(R.id.nav_scan)
    }

    private fun startQrScanner() {
        val options = ScanOptions()
        options.setPrompt("Align QR code inside viewfinder to scan")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        qrScannerLauncher.launch(options)
    }

    private fun handleScannedQrContent(qrContent: String) {
        val currentUser = SessionManager.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Calculate credit reward from QR content or random between 50-500
        val creditToAdd: Double = if (qrContent.contains("CREDIT_")) {
            qrContent.substringAfter("CREDIT_").toDoubleOrNull() ?: (50..500).random().toDouble()
        } else {
            (50..500).random().toDouble()
        }

        lifecycleScope.launch {
            val updatedUser = currentUser.copy(creditBalance = currentUser.creditBalance + creditToAdd)
            SessionManager.currentUser = updatedUser

            val userIdx = SessionManager.usersList.indexOfFirst { it.uid == currentUser.uid }
            if (userIdx != -1) {
                SessionManager.usersList[userIdx] = updatedUser
            }

            val now = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
            val newTx = TransactionItem(
                senderUid = "SYSTEM_QR",
                receiverUid = currentUser.uid,
                senderPhone = "QR Reward",
                receiverPhone = currentUser.phone,
                amount = creditToAdd,
                type = "QR_REDEEM",
                description = "Redeemed QR Credit: ₹$creditToAdd",
                createdAt = now
            )

            SessionManager.transactionsList.add(0, newTx)

            try {
                SupabaseClient.client.from("transactions").insert(newTx)
            } catch (e: Exception) {
                // Ignored if network/table error
            }

            Toast.makeText(this@ScanActivity, "🎉 Successfully Redeemed ₹$creditToAdd Credits!", Toast.LENGTH_LONG).show()
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
