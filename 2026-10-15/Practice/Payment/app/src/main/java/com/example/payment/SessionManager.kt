package com.example.payment

object SessionManager {
    var currentUser: User? = null
    var isAdmin: Boolean = false

    val usersList = mutableListOf<User>()
    val transactionsList = mutableListOf<TransactionItem>()
    val qrCodesList = mutableListOf<QrCodeData>()
    val redeemedQrCodesSet = mutableSetOf<String>()

    init {
        val defaultUser = User(
            uid = "123456789",
            phone = "9876543210",
            password = "Password123",
            firstName = "Shubham",
            lastName = "Sharma",
            creditBalance = 0.0
        )
        usersList.add(defaultUser)
    }

    fun isQrRedeemed(qrId: String): Boolean {
        if (redeemedQrCodesSet.contains(qrId)) return true
        val foundQr = qrCodesList.find { it.qrCodeId == qrId }
        return foundQr?.isRedeemed == true
    }

    fun markQrRedeemed(qrId: String, redeemedByUid: String) {
        redeemedQrCodesSet.add(qrId)
        val index = qrCodesList.indexOfFirst { it.qrCodeId == qrId }
        if (index != -1) {
            qrCodesList[index] = qrCodesList[index].copy(isRedeemed = true, redeemedByUid = redeemedByUid)
        }
    }

    fun generateUnique9DigitUid(): String {
        var newUid: String
        do {
            newUid = (100000000..999999999).random().toString()
        } while (usersList.any { it.uid == newUid })
        return newUid
    }
}
