package com.example.financeapp.data.firebase.dto

data class TransactionFirebaseDto (
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val type: String = ""
)