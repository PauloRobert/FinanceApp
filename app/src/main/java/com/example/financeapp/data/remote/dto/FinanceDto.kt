package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Deposit ────────────────────────────────────────────
data class DepositRequestDto(val amount: Double, @SerializedName("deposit_type") val depositType: String = "PIX", val description: String? = null)
data class DepositDto(val id: String = "", val amount: Double = 0.0, @SerializedName("deposit_type") val depositType: String = "", val status: String = "", val description: String? = null, @SerializedName("created_at") val createdAt: String = "")
data class DepositListDto(val deposits: List<DepositDto> = emptyList(), val total: Int = 0)

// ── Payment ────────────────────────────────────────────
data class PaymentRequestDto(val barcode: String? = null, @SerializedName("payee_name") val payeeName: String, val amount: Double, @SerializedName("due_date") val dueDate: String? = null, @SerializedName("payment_type") val paymentType: String = "BOLETO", val description: String? = null)
data class PaymentDto(val id: String = "", val barcode: String? = null, @SerializedName("payee_name") val payeeName: String = "", val amount: Double = 0.0, @SerializedName("due_date") val dueDate: String? = null, @SerializedName("payment_type") val paymentType: String = "", val status: String = "", val description: String? = null, @SerializedName("created_at") val createdAt: String = "")
data class PaymentListDto(val payments: List<PaymentDto> = emptyList(), val total: Int = 0)

// ── Card ───────────────────────────────────────────────
data class CardDto(val id: String = "", @SerializedName("card_name") val cardName: String = "", @SerializedName("last_four") val lastFour: String = "", @SerializedName("card_type") val cardType: String = "", val status: String = "", @SerializedName("credit_limit") val creditLimit: Double = 0.0, @SerializedName("available_limit") val availableLimit: Double = 0.0, @SerializedName("due_day") val dueDay: String = "10", @SerializedName("is_contactless") val isContactless: Boolean = true, @SerializedName("is_international") val isInternational: Boolean = false)
data class CardCreateDto(@SerializedName("card_name") val cardName: String, @SerializedName("card_type") val cardType: String = "DEBIT", @SerializedName("credit_limit") val creditLimit: Double = 0.0)
data class CardUpdateDto(@SerializedName("is_contactless") val isContactless: Boolean? = null, @SerializedName("is_international") val isInternational: Boolean? = null, val status: String? = null)
data class CardTransactionDto(val id: String = "", val merchant: String = "", val amount: Double = 0.0, val installments: String = "1/1", val category: String? = null, @SerializedName("created_at") val createdAt: String = "")
data class CardTransactionListDto(val transactions: List<CardTransactionDto> = emptyList(), val total: Int = 0)

// ── Investment ─────────────────────────────────────────
data class InvestmentDto(val id: String = "", val name: String = "", @SerializedName("investment_type") val investmentType: String = "", @SerializedName("amount_invested") val amountInvested: Double = 0.0, @SerializedName("current_value") val currentValue: Double = 0.0, @SerializedName("annual_rate") val annualRate: Double = 0.0, @SerializedName("maturity_date") val maturityDate: String? = null, val status: String = "", @SerializedName("created_at") val createdAt: String = "")
data class InvestmentCreateDto(val name: String, @SerializedName("investment_type") val investmentType: String, val amount: Double, @SerializedName("maturity_date") val maturityDate: String? = null)
data class InvestmentListDto(val investments: List<InvestmentDto> = emptyList(), val total: Int = 0)
data class InvestmentDashboardDto(@SerializedName("total_invested") val totalInvested: Double = 0.0, @SerializedName("total_current_value") val totalCurrentValue: Double = 0.0, @SerializedName("total_profit") val totalProfit: Double = 0.0, @SerializedName("profit_percentage") val profitPercentage: Double = 0.0, @SerializedName("investments_count") val investmentsCount: Int = 0)
