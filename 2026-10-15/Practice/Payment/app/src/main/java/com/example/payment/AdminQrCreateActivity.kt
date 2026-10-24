package com.example.payment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
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

            val qrId = "QR_${UUID.randomUUID().toString().take(8)}"
            val qrData = QrCodeData(
                qrCodeId = qrId,
                creditAmount = creditAmount,
                isRedeemed = false
            )

            SessionManager.qrCodesList.add(qrData)

            val qrPayload = HmacQrSigner.signQrPayload(qrId, creditAmount)
            val bitmap = generateQrBitmap(qrPayload)
            if (bitmap != null) {
                ivGeneratedQr?.setImageBitmap(bitmap)
                ivGeneratedQr?.visibility = View.VISIBLE
            }

            tvQrStatus?.text = "Generated Single QR Code: $qrId\nCredit Value: Rs. $creditAmount"
            Toast.makeText(this, "Digitally Signed QR Code Generated!", Toast.LENGTH_SHORT).show()
        }

        btnGenerate100Bulk?.setOnClickListener {
            val bulkList = generate100RandomQrCodes()
            SessionManager.qrCodesList.addAll(bulkList)

            val sampleQr = bulkList.last()
            val samplePayload = HmacQrSigner.signQrPayload(sampleQr.qrCodeId, sampleQr.creditAmount)
            val bitmap = generateQrBitmap(samplePayload)
            if (bitmap != null) {
                ivGeneratedQr?.setImageBitmap(bitmap)
                ivGeneratedQr?.visibility = View.VISIBLE
            }

            tvQrStatus?.text = "Created 100 Signed QR Codes!\nCredits per QR: Rs. 50 to Rs. 500\nTotal Pool: ${SessionManager.qrCodesList.size} QR Codes"
            Toast.makeText(this, "🎉 100 Signed Credit QR Codes Created!", Toast.LENGTH_LONG).show()
        }

        btnExportDocx?.setOnClickListener {
            if (SessionManager.qrCodesList.isEmpty()) {
                val bulkList = generate100RandomQrCodes()
                SessionManager.qrCodesList.addAll(bulkList)
            }

            try {
                val docxFile = DocxExporter.generateQrStickersDocx(this, SessionManager.qrCodesList.take(100))
                val contentUri = FileProvider.getUriForFile(this, "$packageName.provider", docxFile)

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(contentUri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(viewIntent, "Open or Share DOCX Sticker Sheet").apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(shareIntent))
                }

                tvQrStatus?.text = "✅ Exported 100 QR DOCX Stickers (5x6 A4 Grid)!\nFile: ${docxFile.name}"
                Toast.makeText(this, "📄 DOCX Created: ${docxFile.name}", Toast.LENGTH_LONG).show()
                startActivity(chooser)

            } catch (e: Exception) {
                Toast.makeText(this, "Error exporting DOCX: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        btnExportPdf?.setOnClickListener {
            if (SessionManager.qrCodesList.isEmpty()) {
                val bulkList = generate100RandomQrCodes()
                SessionManager.qrCodesList.addAll(bulkList)
            }

            try {
                val pdfFile = PdfExporter.generateQrStickersPdf(this, SessionManager.qrCodesList.take(100))
                val contentUri = FileProvider.getUriForFile(this, "$packageName.provider", pdfFile)

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(contentUri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(viewIntent, "Open or Print QR PDF").apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(shareIntent))
                }

                tvQrStatus?.text = "✅ Created Printable A4 PDF (5x6 Grid)!\nFile: ${pdfFile.name}"
                Toast.makeText(this, "🖨️ PDF Created: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                startActivity(chooser)

            } catch (e: Exception) {
                Toast.makeText(this, "Error exporting PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generate100RandomQrCodes(): List<QrCodeData> {
        val list = mutableListOf<QrCodeData>()
        for (i in 1..100) {
            val randomCredit = (50..500).random().toDouble()
            val qrId = "QR_BULK_${UUID.randomUUID().toString().take(8)}"
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
