package com.example.financeapp.ui.screens.registro

// Estado da tela de registro
data class RegistroUiState(
    val nomeUsuario: String = "",
    val senha: String = "",
    val confirmacaoSenha: String = "",
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val registroRealizado: Boolean = false
)
