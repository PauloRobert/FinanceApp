package com.example.financeapp.ui.screens.pix

import com.example.financeapp.data.remote.dto.PixFavoriteDto

data class PixHubUiState(
    val carregando: Boolean = false,
    val saldo: Double = 0.0,
    val limiteDiario: Double = 20000.0,
    val limiteUsado: Double = 0.0,
    val pixAtivo: Boolean = true,
    val totalEnviadoMes: Double = 0.0,
    val totalRecebidoMes: Double = 0.0,
    val qtdChaves: Int = 0,
    val favoritos: List<PixFavoriteDto> = emptyList(),
    val mensagemErro: String? = null
)
