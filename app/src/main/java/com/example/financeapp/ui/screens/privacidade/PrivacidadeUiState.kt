package com.example.financeapp.ui.screens.privacidade

import com.example.financeapp.data.remote.dto.PrivacyResponse

data class PrivacidadeUiState(
    val carregando: Boolean = false,
    val shareUsageData: Boolean = false,
    val showBalanceOnHome: Boolean = true,
    val transactionNotifications: Boolean = true,
    val marketingEmails: Boolean = false,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
)

fun PrivacyResponse.toUiState() = PrivacidadeUiState(
    shareUsageData = shareUsageData,
    showBalanceOnHome = showBalanceOnHome,
    transactionNotifications = transactionNotifications,
    marketingEmails = marketingEmails
)
