package com.example.payment

object AdminConfig {
    const val ADMIN_PHONE = "9761611492"
    const val ADMIN_PASS = "Nishan@123"

    fun verifyAdminCredentials(phoneInput: String, passwordInput: String): Boolean {
        val cleanPhone = phoneInput.trim()
        val cleanPass = passwordInput.trim()
        return cleanPhone == ADMIN_PHONE && cleanPass == ADMIN_PASS
    }
}
