package com.example.financeapp.ui.screens.login

// Estado da tela de login
data class LoginUiState(
    val nomeUsuario: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val loginRealizado: Boolean = false
)
