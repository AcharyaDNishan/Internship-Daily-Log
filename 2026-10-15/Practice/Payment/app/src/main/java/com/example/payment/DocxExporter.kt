package com.example.payment

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocxExporter {

    fun generateQrStickersDocx(context: Context, qrList: List<QrCodeData>): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val outputFile = File(outputDir, "qr_stickers_5x6_a4.docx")
        if (outputFile.exists()) outputFile.delete()

        val imageMap = mutableMapOf<String, ByteArray>()
        val relsList = mutableListOf<Pair<String, String>>()

        val encoder = BarcodeEncoder()
        qrList.forEachIndexed { index, qr ->
            val payload = "QR_PAY_${qr.qrCodeId}_CREDIT_${qr.creditAmount}"
            val bitmap = encoder.encodeBitmap(payload, BarcodeFormat.QR_CODE, 180, 180)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val mediaName = "image${index + 1}.png"
            val rId = "rId${index + 100}"
            imageMap[mediaName] = stream.toByteArray()
            relsList.add(rId to mediaName)
        }

        val docXml = StringBuilder()
        docXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        docXml.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\">\n")
        docXml.append("<w:body>\n")

        val pages = qrList.chunked(30)

        pages.forEachIndexed { pageIdx, pageItems ->
            docXml.append("<w:tbl>\n")
            docXml.append("<w:tblPr><w:tblW w:w=\"0\" w:type=\"auto\"/><w:tblBorders><w:top w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:left w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:right w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:insideH w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/><w:insideV w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"CCCCCC\"/></w:tblBorders></w:tblPr>\n")

            val rows = pageItems.chunked(5)
            rows.forEach { rowItems ->
                docXml.append("<w:tr>\n")
                rowItems.forEach { item ->
                    val globalIdx = qrList.indexOf(item)
                    val rId = "rId${globalIdx + 100}"

                    docXml.append("<w:tc><w:tcPr><w:tcW w:w=\"1900\" w:type=\"dxa\"/><w:vAlign w:val=\"center\"/></w:tcPr>\n")
                    docXml.append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr>\n")
                    docXml.append("<w:r><w:drawing><wp:inline><wp:extent cx=\"450000\" cy=\"450000\"/><wp:docPr id=\"${globalIdx + 1}\" name=\"QR\"/><a:graphic><a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"><pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"><pic:nvPicPr><pic:cNvPr id=\"${globalIdx + 1}\" name=\"QR\"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip r:embed=\"$rId\"/></pic:blipFill><pic:spPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"450000\" cy=\"450000\"/></a:xfrm><a:prstGeom prst=\"rect\"/></pic:spPr></pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p>\n")
                    docXml.append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"16\"/></w:rPr><w:t>Rs. ${item.creditAmount.toInt()}</w:t></w:r></w:p>\n")
                    docXml.append("</w:tc>\n")
                }

                val missingCells = 5 - rowItems.size
                for (i in 0 until missingCells) {
                    docXml.append("<w:tc><w:tcPr><w:tcW w:w=\"1900\" w:type=\"dxa\"/></w:tcPr><w:p/></w:tc>\n")
                }

                docXml.append("</w:tr>\n")
            }

            docXml.append("</w:tbl>\n")

            if (pageIdx < pages.size - 1) {
                docXml.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>\n")
            }
        }

        docXml.append("</w:body></w:document>")

        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            zip.putNextEntry(ZipEntry("[Content_Types].xml"))
            val contentTypes = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Default Extension="png" ContentType="image/png"/>
<Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>""".toByteArray()
            zip.write(contentTypes)
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("_rels/.rels"))
            val rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>""".toByteArray()
            zip.write(rels)
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("word/document.xml"))
            zip.write(docXml.toString().toByteArray())
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("word/_rels/document.xml.rels"))
            val docRels = StringBuilder()
            docRels.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
            docRels.append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n")
            relsList.forEach { (rId, mediaName) ->
                docRels.append("<Relationship Id=\"$rId\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"media/$mediaName\"/>\n")
            }
            docRels.append("</Relationships>")
            zip.write(docRels.toString().toByteArray())
            zip.closeEntry()

            imageMap.forEach { (mediaName, bytes) ->
                zip.putNextEntry(ZipEntry("word/media/$mediaName"))
                zip.write(bytes)
                zip.closeEntry()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "qr_stickers_5x6_a4.docx")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        outputFile.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                }
            } catch (e: Exception) {
                // Non-critical fallback
            }
        }

        return outputFile
    }
}
