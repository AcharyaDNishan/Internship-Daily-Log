package com.example.payment

import java.security.MessageDigest

object AdminConfig {
    // Obfuscated / Hashed credentials (Phone: 9761611492, Password: Nishan@123)
    private const val ADMIN_PHONE_HASH = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918" // SHA-256 for 9761611492
    private const val ADMIN_PASS_HASH = "7a4198ed61129671db3f89839eb721e0ebfffa1815e9851f506048d08cb51f52" // SHA-256 for Nishan@123

    fun verifyAdminCredentials(phoneInput: String, passwordInput: String): Boolean {
        val phoneHash = hashSha256(phoneInput.trim())
        val passHash = hashSha256(passwordInput.trim())
        return phoneHash == ADMIN_PHONE_HASH && passHash == ADMIN_PASS_HASH
    }

    private fun hashSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
