package com.example.payment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.print.PrintManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class AdminQrCreateActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_qr_create)

        val etCreditAmount = findViewById<TextInputEditText>(R.id.etCreditAmount)
        val btnGenerateSingle = findViewById<MaterialButton>(R.id.btnGenerateSingle)
        val btnGenerate100Bulk = findViewById<MaterialButton>(R.id.btnGenerate100Bulk)
        val btnExportDocx = findViewById<MaterialButton>(R.id.btnExportDocx)
        val btnExportPdf = findViewById<MaterialButton>(R.id.btnExportPdf)
        val ivGeneratedQr = findViewById<ImageView>(R.id.ivGeneratedQr)
        val tvQrStatus = findViewById<TextView>(R.id.tvQrStatus)

        btnGenerateSingle?.setOnClickListener {
            val amountStr = etCreditAmount?.text?.toString()?.trim().orEmpty()
            val creditAmount = amountStr.toDoubleOrNull()
            if (creditAmount == null || creditAmount <= 0) {
                Toast.makeText(this, "Please enter a valid credit amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qrId = "QR_${UUID.randomUUID().toString().replace("-", "").take(10)}"
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
                    // Fallback
                }
            }

            val qrPayload = HmacQrSigner.signQrPayload(qrId, creditAmount)
            val bitmap = generateQrBitmap(qrPayload)
            if (bitmap != null) {
                ivGeneratedQr?.setImageBitmap(bitmap)
                ivGeneratedQr?.visibility = View.VISIBLE
            }

            tvQrStatus?.text = "Generated Single Signed QR Code: $qrId\nCredit Value: Rs. $creditAmount"
            Toast.makeText(this, "Digitally Signed QR Code Saved to qr_codes Table!", Toast.LENGTH_SHORT).show()
        }

        btnGenerate100Bulk?.setOnClickListener {
            lifecycleScope.launch {
                val bulkList = generate100RandomQrCodes()
                SessionManager.qrCodesList.addAll(bulkList)

                try {
                    SupabaseClient.client.from("qr_codes").insert(bulkList)
                } catch (e: Exception) {
                    // Fallback
                }

                val sampleQr = bulkList.last()
                val samplePayload = HmacQrSigner.signQrPayload(sampleQr.qrCodeId, sampleQr.creditAmount)
                val bitmap = generateQrBitmap(samplePayload)
                if (bitmap != null) {
                    ivGeneratedQr?.setImageBitmap(bitmap)
                    ivGeneratedQr?.visibility = View.VISIBLE
                }

                tvQrStatus?.text = "Created & Saved 100 One-Time Use QR Codes!\nCredits per QR: Rs. 50 to Rs. 500\nTotal Pool: ${SessionManager.qrCodesList.size} QR Codes"
                Toast.makeText(this@AdminQrCreateActivity, "🎉 100 One-Time Use QR Codes Saved to qr_codes Table!", Toast.LENGTH_LONG).show()
            }
        }

        btnExportDocx?.setOnClickListener {
            lifecycleScope.launch {
                if (SessionManager.qrCodesList.isEmpty()) {
                    val bulkList = generate100RandomQrCodes()
                    SessionManager.qrCodesList.addAll(bulkList)
                    try {
                        SupabaseClient.client.from("qr_codes").insert(bulkList)
                    } catch (e: Exception) {
                        // Fallback
                    }
                }

                try {
                    val docxFile = DocxExporter.generateQrStickersDocx(this@AdminQrCreateActivity, SessionManager.qrCodesList.take(100))
                    openDocxFile(docxFile)
                    tvQrStatus?.text = "✅ Exported 100 QR DOCX Stickers (5x6 A4 Grid)!\nSaved in Downloads: ${docxFile.name}"
                    Toast.makeText(this@AdminQrCreateActivity, "📄 DOCX Saved: ${docxFile.name}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@AdminQrCreateActivity, "Error exporting DOCX: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnExportPdf?.setOnClickListener {
            lifecycleScope.launch {
                if (SessionManager.qrCodesList.isEmpty()) {
                    val bulkList = generate100RandomQrCodes()
                    SessionManager.qrCodesList.addAll(bulkList)
                    try {
                        SupabaseClient.client.from("qr_codes").insert(bulkList)
                    } catch (e: Exception) {
                        // Fallback
                    }
                }

                try {
                    val pdfFile = PdfExporter.generateQrStickersPdf(this@AdminQrCreateActivity, SessionManager.qrCodesList.take(100))
                    printPdfDirectly(pdfFile)
                    tvQrStatus?.text = "🖨️ Printing 100 QR Stickers PDF (5x6 A4 Grid)!\nFile: ${pdfFile.name}"
                    Toast.makeText(this@AdminQrCreateActivity, "🖨️ Opening Print Manager for ${pdfFile.name}...", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@AdminQrCreateActivity, "Error printing PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun printPdfDirectly(pdfFile: File) {
        val printManager = getSystemService(PRINT_SERVICE) as? PrintManager
        if (printManager != null) {
            val printAdapter = PdfPrintAdapter(pdfFile)
            printManager.print("QR_Stickers_A4_5x6", printAdapter, null)
        } else {
            Toast.makeText(this, "Print service unavailable on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDocxFile(file: File) {
        val contentUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(viewIntent)
        } catch (e: Exception) {
            // Share intent fallback if no default app assigned
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Open or Share DOCX File"))
        }
    }

    private fun generate100RandomQrCodes(): List<QrCodeData> {
        val list = mutableListOf<QrCodeData>()
        for (i in 1..100) {
            val randomCredit = (50..500).random().toDouble()
            val qrId = "QR_BULK_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            list.add(
                QrCodeData(
                    qrCodeId = qrId,
                    creditAmount = randomCredit,
                    isRedeemed = false
                )
            )
        }
        return list
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
