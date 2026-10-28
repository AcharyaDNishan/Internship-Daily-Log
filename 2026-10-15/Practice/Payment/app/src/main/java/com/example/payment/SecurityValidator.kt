package com.example.payment

object SecurityValidator {

    private val attemptMap = mutableMapOf<String, MutableList<Long>>()
    private val failedLoginCountMap = mutableMapOf<String, Int>()
    private val lockoutTimeMap = mutableMapOf<String, Long>()

    private const val MAX_ATTEMPTS = 5
    private const val WINDOW_MS = 60_000L // 1 minute rate limit window
    private const val LOCKOUT_DURATION_MS = 300_000L // 5 minutes account lockout

    fun isRateLimited(actionKey: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val attempts = attemptMap.getOrPut(actionKey) { mutableListOf() }
        attempts.removeAll { currentTime - it > WINDOW_MS }

        if (attempts.size >= MAX_ATTEMPTS) {
            return true
        }

        attempts.add(currentTime)
        return false
    }

    fun isAccountLockedOut(phone: String): Boolean {
        val lockTime = lockoutTimeMap[phone] ?: return false
        val currentTime = System.currentTimeMillis()
        if (currentTime - lockTime < LOCKOUT_DURATION_MS) {
            return true // Account is temporarily locked
        } else {
            // Lockout expired
            lockoutTimeMap.remove(phone)
            failedLoginCountMap.remove(phone)
            return false
        }
    }

    fun recordFailedLogin(phone: String) {
        val count = (failedLoginCountMap[phone] ?: 0) + 1
        failedLoginCountMap[phone] = count
        if (count >= 5) {
            lockoutTimeMap[phone] = System.currentTimeMillis()
        }
    }

    fun resetFailedLogin(phone: String) {
        failedLoginCountMap.remove(phone)
        lockoutTimeMap.remove(phone)
    }

    fun validatePhoneNumber(phone: String): String? {
        val sanitized = phone.trim()
        if (sanitized.isEmpty()) {
            return "Phone number is required"
        }
        if (!sanitized.matches(Regex("^[0-9]{10}$"))) {
            return "Please enter a valid 10-digit phone number"
        }
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.trim().isEmpty()) {
            return "Password is required"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        return null
    }

    fun validateName(name: String, fieldName: String = "Name"): String? {
        val sanitized = name.trim()
        if (sanitized.isEmpty()) {
            return "$fieldName is required"
        }
        if (sanitized.length < 2) {
            return "$fieldName must be at least 2 characters"
        }
        return null
    }

    fun sanitizeText(input: String): String {
        return input.replace(Regex("[<>&\"']"), "").trim()
    }
}
