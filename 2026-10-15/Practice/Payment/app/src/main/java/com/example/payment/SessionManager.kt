package com.example.payment

import java.util.UUID

object SessionManager {
    var currentUser: User? = null
    var isAdmin: Boolean = false

    // Local fallback in-memory databases for seamless operation
    val usersList = mutableListOf<User>()
    val transactionsList = mutableListOf<TransactionItem>()
    val qrCodesList = mutableListOf<QrCodeData>()

    fun generateUnique9DigitUid(): String {
        var newUid: String
        do {
            newUid = (100000000..999999999).random().toString()
        } while (usersList.any { it.uid == newUid })
        return newUid
    }
}
