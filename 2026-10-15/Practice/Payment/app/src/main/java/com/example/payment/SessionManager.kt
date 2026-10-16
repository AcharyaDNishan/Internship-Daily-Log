package com.example.payment

object SessionManager {
    var currentUser: User? = null
    var isAdmin: Boolean = false

    val usersList = mutableListOf<User>()
    val transactionsList = mutableListOf<TransactionItem>()
    val qrCodesList = mutableListOf<QrCodeData>()

    init {
        val defaultUser = User(
            uid = "123456789",
            phone = "9876543210",
            password = "Password123",
            firstName = "Shubham",
            lastName = "Sharma",
            creditBalance = 0.0 // Account starts at zero
        )
        usersList.add(defaultUser)
    }

    fun generateUnique9DigitUid(): String {
        var newUid: String
        do {
            newUid = (100000000..999999999).random().toString()
        } while (usersList.any { it.uid == newUid })
        return newUid
    }
}
