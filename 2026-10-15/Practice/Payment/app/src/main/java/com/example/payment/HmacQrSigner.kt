package com.example.payment

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacQrSigner {

    private const val HMAC_SECRET_KEY = "HMAC_SECRET_PAYMENT_APP_SECURE_KEY_2026"

    fun signQrPayload(qrId: String, creditAmount: Double): String {
        val rawData = "$qrId:$creditAmount"
        val signature = computeHmacSha256(rawData, HMAC_SECRET_KEY)
        return "QR_PAY_${qrId}_CREDIT_${creditAmount}_SIG_$signature"
    }

    fun verifyAndExtractQrCredit(payload: String): Double? {
        try {
            if (!payload.contains("_CREDIT_") || !payload.contains("_SIG_")) {
                return null // Malformed payload
            }

            val qrId = payload.substringAfter("QR_PAY_").substringBefore("_CREDIT_")
            val amountStr = payload.substringAfter("_CREDIT_").substringBefore("_SIG_")
            val creditAmount = amountStr.toDoubleOrNull() ?: return null
            val signatureInPayload = payload.substringAfter("_SIG_")

            val rawData = "$qrId:$creditAmount"
            val expectedSignature = computeHmacSha256(rawData, HMAC_SECRET_KEY)

            // Secure Constant-Time Signature Equality Check
            if (constantTimeEquals(signatureInPayload, expectedSignature)) {
                return creditAmount
            }
        } catch (e: Exception) {
            return null
        }
        return null // Invalid / Tampered signature
    }

    private fun computeHmacSha256(data: String, secret: String): String {
        val secretKey = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}
