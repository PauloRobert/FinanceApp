package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO de resposta de uma transação da API
data class TransactionRemoteDto(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val type: String = ""
)

// DTO de resposta da lista de transações
data class TransactionListResponse(
    val transactions: List<TransactionRemoteDto> = emptyList(),
    val total: Int = 0
)

// DTO para criação/atualização de transação
data class TransactionRequest(
    val description: String,
    val amount: Double,
    val date: String,
    val type: String
)

// DTO de login
data class LoginRequest(
    val username: String,
    val password: String
)

// DTO de resposta do token
data class TokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String = "bearer"
)

