package com.example.financeapp.ui.screens.transferencia

import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.TransferResponseDto

data class TransferenciaHubUiState(
    val carregando: Boolean = false,
    val saldo: Double = 0.0,
    val limite: Double = 50000.0,
    val ultimaData: String? = null,
    val ultimoValor: Double? = null,
    val totalMes: Double = 0.0,
    val agendamentos: Int = 0,
    val favorecidos: List<BeneficiaryDto> = emptyList(),
    val historico: List<TransferResponseDto> = emptyList(),
    val mensagemErro: String? = null
)
