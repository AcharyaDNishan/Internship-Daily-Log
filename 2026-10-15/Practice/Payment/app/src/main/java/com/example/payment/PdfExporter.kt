package com.example.payment

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    // A4 dimensions at 72 DPI: 595 x 842 points
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    fun generateQrStickersPdf(context: Context, qrList: List<QrCodeData>): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val outputFile = File(outputDir, "qr_stickers_5x6_a4.pdf")
        if (outputFile.exists()) outputFile.delete()

        val pdfDocument = PdfDocument()
        val encoder = BarcodeEncoder()

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val paintBorder = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        // Split into pages of 30 items (5 cols x 6 rows)
        val pages = qrList.chunked(30)

        pages.forEachIndexed { pageIdx, pageItems ->
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIdx + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val startX = 25f
            val startY = 30f
            val cellWidth = 108f
            val cellHeight = 125f

            pageItems.forEachIndexed { idx, item ->
                val col = idx % 5
                val row = idx / 5

                val left = startX + (col * cellWidth)
                val top = startY + (row * cellHeight)
                val right = left + cellWidth - 4f
                val bottom = top + cellHeight - 4f

                // Draw sticker cell border
                canvas.drawRoundRect(left, top, right, bottom, 6f, 6f, paintBorder)

                // Render QR code
                val payload = HmacQrSigner.signQrPayload(item.qrCodeId, item.creditAmount)
                val bitmap = encoder.encodeBitmap(payload, BarcodeFormat.QR_CODE, 140, 140)

                canvas.drawBitmap(bitmap, null,
                    RectF(left + 14f, top + 10f, right - 14f, bottom - 30f), null)

                // Draw credit label
                val textX = (left + right) / 2f
                val textY = bottom - 10f
                canvas.drawText("Rs. ${item.creditAmount.toInt()}", textX, textY, paintText)
            }

            pdfDocument.finishPage(page)
        }

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
