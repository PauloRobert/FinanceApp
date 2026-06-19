package com.example.financeapp.ui.screens.transferencia

import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.TransferResponseDto

data class TransferenciaEnviarUiState(
    // Step 1 — destinatário
    val favorecidos: List<BeneficiaryDto> = emptyList(),
    val buscaFavorecido: String = "",
    val carregandoFavorecidos: Boolean = false,
    val favorecidoSelecionado: BeneficiaryDto? = null,
    // Manual
    val nomeDestinatario: String = "",
    val bancoDestinatario: String = "",
    val agenciaDestinatario: String = "",
    val contaDestinatario: String = "",
    val tipoConta: String = "CORRENTE",
    // Step 2 — valor
    val valor: String = "",
    val descricao: String = "",
    // Step 3 — confirmação
    val enviando: Boolean = false,
    // Step 4 — comprovante
    val comprovante: TransferResponseDto? = null,
    // Meta
    val etapa: TransferenciaEtapa = TransferenciaEtapa.DESTINATARIO,
    val mensagemErro: String? = null
)

enum class TransferenciaEtapa {
    DESTINATARIO, VALOR, CONFIRMACAO, COMPROVANTE
}
