package com.example.financeapp.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction (
    val id: String = "",
    val description: String,
    val amount: BigDecimal,
    val date: LocalDateTime,
    val type: TransactionType
)

enum class TransactionType{
    INCOME,
    EXPENSE
}