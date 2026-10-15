package com.example.payment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.github.jan_tennert.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.UUID

class AdminQrCreateActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_qr_create)

        val etCreditAmount = findViewById<TextInputEditText>(R.id.etCreditAmount)
        val btnGenerateSingle = findViewById<MaterialButton>(R.id.btnGenerateSingle)
        val btnGenerate100Bulk = findViewById<MaterialButton>(R.id.btnGenerate100Bulk)
        val ivGeneratedQr = findViewById<ImageView>(R.id.ivGeneratedQr)
        val tvQrStatus = findViewById<TextView>(R.id.tvQrStatus)

        btnGenerateSingle?.setOnClickListener {
            val amountStr = etCreditAmount?.text?.toString()?.trim().orEmpty()
            val creditAmount = amountStr.toDoubleOrNull()
            if (creditAmount == null || creditAmount <= 0) {
                Toast.makeText(this, "Please enter a valid credit amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qrId = "QR_${UUID.randomUUID().toString().take(8)}"
            val qrData = QrCodeData(
                qrCodeId = qrId,
                creditAmount = creditAmount,
                isRedeemed = false
            )

            SessionManager.qrCodesList.add(qrData)
            lifecycleScope.launch {
                try {
                    SupabaseClient.client.from("qr_codes").insert(qrData)
                } catch (e: Exception) {
                    // Ignored if network/table error
                }
            }

            val qrPayload = "QR_PAY_${qrId}_CREDIT_$creditAmount"
            val bitmap = generateQrBitmap(qrPayload)
            if (bitmap != null) {
                ivGeneratedQr?.setImageBitmap(bitmap)
                ivGeneratedQr?.visibility = View.VISIBLE
            }

            tvQrStatus?.text = "Generated Single QR Code: $qrId\nCredit Value: ₹$creditAmount"
            Toast.makeText(this, "Single QR Code Generated!", Toast.LENGTH_SHORT).show()
        }

        btnGenerate100Bulk?.setOnClickListener {
            val bulkList = mutableListOf<QrCodeData>()
            for (i in 1..100) {
                val randomCredit = (50..500).random().toDouble()
                val qrId = "QR_BULK_${UUID.randomUUID().toString().take(8)}"
                val qrData = QrCodeData(
                    qrCodeId = qrId,
                    creditAmount = randomCredit,
                    isRedeemed = false
                )
                bulkList.add(qrData)
            }

            SessionManager.qrCodesList.addAll(bulkList)
            lifecycleScope.launch {
                try {
                    SupabaseClient.client.from("qr_codes").insert(bulkList)
                } catch (e: Exception) {
                    // Ignored if network/table error
                }
            }

            // Display sample preview of the 100th generated QR code
            val sampleQr = bulkList.last()
            val samplePayload = "QR_PAY_${sampleQr.qrCodeId}_CREDIT_${sampleQr.creditAmount}"
            val bitmap = generateQrBitmap(samplePayload)
            if (bitmap != null) {
                ivGeneratedQr?.setImageBitmap(bitmap)
                ivGeneratedQr?.visibility = View.VISIBLE
            }

            tvQrStatus?.text = "Successfully Created 100 QR Codes!\nCredits per QR range from ₹50 to ₹500.\nTotal Bulk Pool: 100 QR Codes"
            Toast.makeText(this, "🎉 100 Random Credit QR Codes Created!", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateQrBitmap(content: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 500, 500)
        } catch (e: Exception) {
            null
        }
    }
}
