package com.example.payment

object SecurityValidator {

    private val attemptMap = mutableMapOf<String, MutableList<Long>>()
    private const val MAX_ATTEMPTS = 5
    private const val WINDOW_MS = 60_000L // 1 minute window

    // Client-side rate limiting to prevent spam / brute force
    fun isRateLimited(actionKey: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val attempts = attemptMap.getOrPut(actionKey) { mutableListOf() }

        // Remove attempts older than 1 minute
        attempts.removeAll { currentTime - it > WINDOW_MS }

        if (attempts.size >= MAX_ATTEMPTS) {
            return true // Rate limited!
        }

        attempts.add(currentTime)
        return false
    }

    fun validatePhoneNumber(phone: String): String? {
        val sanitized = phone.trim()
        if (sanitized.isEmpty()) {
            return "Phone number is required"
        }
        if (!sanitized.matches(Regex("^[0-9]{10}$"))) {
            return "Please enter a valid 10-digit phone number"
        }
        return null // Valid
    }

    fun validatePassword(password: String): String? {
        if (password.trim().isEmpty()) {
            return "Password is required"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        return null // Valid
    }

    fun validateName(name: String, fieldName: String = "Name"): String? {
        val sanitized = name.trim()
        if (sanitized.isEmpty()) {
            return "$fieldName is required"
        }
        if (sanitized.length < 2) {
            return "$fieldName must be at least 2 characters"
        }
        return null // Valid
    }

    fun sanitizeText(input: String): String {
        return input.replace(Regex("[<>&\"']"), "").trim()
    }
}
