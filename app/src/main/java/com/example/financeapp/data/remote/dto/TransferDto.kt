package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Dashboard ──────────────────────────────────────────

data class TransferDashboardDto(
    val balance: Double = 0.0,
    @SerializedName("transfer_limit")
    val transferLimit: Double = 50000.0,
    @SerializedName("last_transfer_date")
    val lastTransferDate: String? = null,
    @SerializedName("last_transfer_amount")
    val lastTransferAmount: Double? = null,
    @SerializedName("total_transferred_month")
    val totalTransferredMonth: Double = 0.0,
    @SerializedName("scheduled_count")
    val scheduledCount: Int = 0
)

// ── Beneficiary ────────────────────────────────────────

data class BeneficiaryDto(
    val id: String = "",
    val name: String = "",
    @SerializedName("cpf_cnpj")
    val cpfCnpj: String? = null,
    @SerializedName("bank_name")
    val bankName: String = "",
    val agency: String? = null,
    val account: String = "",
    @SerializedName("account_type")
    val accountType: String = "CORRENTE",
    val nickname: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true
)

data class BeneficiaryCreateRequest(
    val name: String,
    @SerializedName("cpf_cnpj")
    val cpfCnpj: String? = null,
    @SerializedName("bank_name")
    val bankName: String,
    val agency: String? = null,
    val account: String,
    @SerializedName("account_type")
    val accountType: String = "CORRENTE",
    val nickname: String? = null
)

// ── Transfer ───────────────────────────────────────────

data class TransferSendDto(
    @SerializedName("beneficiary_id")
    val beneficiaryId: String? = null,
    @SerializedName("receiver_name")
    val receiverName: String,
    @SerializedName("receiver_bank")
    val receiverBank: String,
    @SerializedName("receiver_agency")
    val receiverAgency: String? = null,
    @SerializedName("receiver_account")
    val receiverAccount: String,
    @SerializedName("receiver_account_type")
    val receiverAccountType: String = "CORRENTE",
    val amount: Double,
    val description: String? = null,
    @SerializedName("scheduled_date")
    val scheduledDate: String? = null
)

data class TransferResponseDto(
    val id: String = "",
    @SerializedName("receiver_name")
    val receiverName: String = "",
    @SerializedName("receiver_bank")
    val receiverBank: String = "",
    @SerializedName("receiver_agency")
    val receiverAgency: String? = null,
    @SerializedName("receiver_account")
    val receiverAccount: String = "",
    @SerializedName("receiver_account_type")
    val receiverAccountType: String = "",
    val amount: Double = 0.0,
    val description: String? = null,
    val status: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("beneficiary_id")
    val beneficiaryId: String? = null
)

data class TransferListDto(
    val transfers: List<TransferResponseDto> = emptyList(),
    val total: Int = 0
)
