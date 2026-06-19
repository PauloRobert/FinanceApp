package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Dashboard ──────────────────────────────────────────

data class PixDashboardResponse(
    val balance: Double = 0.0,
    @SerializedName("daily_limit")
    val dailyLimit: Double = 20000.0,
    @SerializedName("daily_used")
    val dailyUsed: Double = 0.0,
    @SerializedName("pix_active")
    val pixActive: Boolean = true,
    @SerializedName("total_sent_month")
    val totalSentMonth: Double = 0.0,
    @SerializedName("total_received_month")
    val totalReceivedMonth: Double = 0.0,
    @SerializedName("keys_count")
    val keysCount: Int = 0
)

// ── Keys ───────────────────────────────────────────────

data class PixKeyCreateRequest(
    @SerializedName("key_type")
    val keyType: String,
    @SerializedName("key_value")
    val keyValue: String
)

data class PixKeyDto(
    val id: String = "",
    @SerializedName("key_type")
    val keyType: String = "",
    @SerializedName("key_value")
    val keyValue: String = "",
    @SerializedName("is_active")
    val isActive: Boolean = true
)

// ── Lookup ─────────────────────────────────────────────

data class PixKeyLookupResponse(
    @SerializedName("key_value")
    val keyValue: String = "",
    @SerializedName("owner_name")
    val ownerName: String = "",
    val bank: String = "IF Bank"
)

// ── Transfer ───────────────────────────────────────────

data class PixSendRequest(
    @SerializedName("receiver_key")
    val receiverKey: String,
    val amount: Double,
    val description: String? = null,
    @SerializedName("scheduled_date")
    val scheduledDate: String? = null
)

data class PixTransferDto(
    val id: String = "",
    @SerializedName("receiver_key")
    val receiverKey: String = "",
    @SerializedName("receiver_name")
    val receiverName: String? = null,
    @SerializedName("receiver_bank")
    val receiverBank: String? = null,
    val amount: Double = 0.0,
    val description: String? = null,
    val status: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("is_favorite")
    val isFavorite: Boolean = false
)

data class PixTransferListResponse(
    val transfers: List<PixTransferDto> = emptyList(),
    val total: Int = 0
)

// ── Favorites ──────────────────────────────────────────

data class PixFavoriteDto(
    @SerializedName("receiver_key")
    val receiverKey: String = "",
    @SerializedName("receiver_name")
    val receiverName: String? = null,
    @SerializedName("receiver_bank")
    val receiverBank: String? = null,
    @SerializedName("last_amount")
    val lastAmount: Double = 0.0
)
