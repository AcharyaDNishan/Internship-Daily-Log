package com.example.payment

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH = 256

    fun hashPassword(password: String, salt: String = "PAYMENT_SALT_SECURE_2026"): String {
        return try {
            val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(Charsets.UTF_8), ITERATIONS, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback SHA-256
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest("$salt:$password".toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        }
    }

    fun verifyPassword(passwordInput: String, storedHash: String, salt: String = "PAYMENT_SALT_SECURE_2026"): Boolean {
        val inputHash = hashPassword(passwordInput, salt)
        return inputHash == storedHash
    }
}
