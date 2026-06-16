package com.example.financeapp.ui.screens.pix

import com.example.financeapp.data.remote.dto.PixKeyLookupResponse
import com.example.financeapp.data.remote.dto.PixTransferDto

data class PixEnviarUiState(
    // Step 1 — busca
    val chaveDestino: String = "",
    val buscando: Boolean = false,
    val destinatario: PixKeyLookupResponse? = null,
    // Step 2 — valor
    val valor: String = "",
    val valorFormatado: String = "",
    val descricao: String = "",
    // Step 3 — confirmação
    val enviando: Boolean = false,
    // Step 4 — comprovante
    val comprovante: PixTransferDto? = null,
    // Meta
    val etapa: PixEnviarEtapa = PixEnviarEtapa.BUSCA,
    val mensagemErro: String? = null
)

enum class PixEnviarEtapa {
    BUSCA, VALOR, CONFIRMACAO, COMPROVANTE
}
