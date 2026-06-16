package com.example.financeapp.ui.screens.perfil

import com.example.financeapp.data.remote.dto.ProfileResponse

data class PerfilUiState(
    val carregando: Boolean = false,
    val salvando: Boolean = false,
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
)

fun ProfileResponse.toUiState() = PerfilUiState(
    username = username,
    displayName = displayName ?: "",
    email = email ?: "",
    phone = phone ?: ""
)
