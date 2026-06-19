package com.example.financeapp.ui.screens.seguranca

import com.example.financeapp.data.remote.dto.SecurityResponse

data class SegurancaUiState(
    val carregando: Boolean = false,
    val salvando: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val loginNotificationEnabled: Boolean = true,
    val lastPasswordChange: String? = null,
    // Troca de senha
    val mostrarTrocaSenha: Boolean = false,
    val senhaAtual: String = "",
    val novaSenha: String = "",
    val confirmarNovaSenha: String = "",
    val trocandoSenha: Boolean = false,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
)

fun SecurityResponse.toUiState() = SegurancaUiState(
    twoFactorEnabled = twoFactorEnabled,
    biometricEnabled = biometricEnabled,
    loginNotificationEnabled = loginNotificationEnabled,
    lastPasswordChange = lastPasswordChange
)
