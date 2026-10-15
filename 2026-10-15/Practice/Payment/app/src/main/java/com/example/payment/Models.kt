package com.example.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val uid: String = "",
    val phone: String = "",
    val password: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("credit_balance") val creditBalance: Double = 0.0
)

@Serializable
data class TransactionItem(
    val id: String? = null,
    @SerialName("sender_uid") val senderUid: String = "",
    @SerialName("receiver_uid") val receiverUid: String = "",
    @SerialName("sender_phone") val senderPhone: String = "",
    @SerialName("receiver_phone") val receiverPhone: String = "",
    val amount: Double = 0.0,
    val type: String = "SENT", // "SENT", "RECEIVED", "QR_REDEEM"
    val description: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class QrCodeData(
    val id: String? = null,
    @SerialName("qr_code_id") val qrCodeId: String = "",
    @SerialName("credit_amount") val creditAmount: Double = 0.0,
    @SerialName("is_redeemed") val isRedeemed: Boolean = false,
    @SerialName("redeemed_by_uid") val redeemedByUid: String? = null
)
