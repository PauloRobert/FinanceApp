package com.example.financeapp.domain.model

// Modelo de domínio que representa o usuário autenticado
data class Usuario(
    val id: String,
    val nomeUsuario: String,
    val ativo: Boolean = true
)
