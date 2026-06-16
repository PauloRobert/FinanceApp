package com.example.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO para requisição de registro
data class RegisterRequest(
    val username: String,
    val password: String
)

// DTO de resposta do registro (dados do usuário criado)
data class UserResponse(
    val id: String,
    val username: String,
    @SerializedName("is_active")
    val isActive: Boolean = true
)
