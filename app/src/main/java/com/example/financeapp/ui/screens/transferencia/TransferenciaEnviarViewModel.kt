package com.example.financeapp.ui.screens.transferencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.TransferSendDto
import com.example.financeapp.domain.repository.TransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransferenciaEnviarViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(TransferenciaEnviarUiState())
    val estado: StateFlow<TransferenciaEnviarUiState> = _estado.asStateFlow()

    init { carregarFavorecidos() }

    private fun carregarFavorecidos() {
        viewModelScope.launch {
            _estado.update { it.copy(carregandoFavorecidos = true) }
            try {
                val lista = transferRepository.listarFavorecidos()
                _estado.update { it.copy(carregandoFavorecidos = false, favorecidos = lista) }
            } catch (e: Exception) {
                _estado.update { it.copy(carregandoFavorecidos = false) }
            }
        }
    }

    fun atualizarBusca(valor: String) { _estado.update { it.copy(buscaFavorecido = valor) } }

    fun selecionarFavorecido(fav: BeneficiaryDto) {
        _estado.update {
            it.copy(
                favorecidoSelecionado = fav,
                nomeDestinatario = fav.name,
                bancoDestinatario = fav.bankName,
                agenciaDestinatario = fav.agency ?: "",
                contaDestinatario = fav.account,
                tipoConta = fav.accountType,
                etapa = TransferenciaEtapa.VALOR
            )
        }
    }

    fun atualizarNome(v: String) { _estado.update { it.copy(nomeDestinatario = v) } }
    fun atualizarBanco(v: String) { _estado.update { it.copy(bancoDestinatario = v) } }
    fun atualizarAgencia(v: String) { _estado.update { it.copy(agenciaDestinatario = v) } }
    fun atualizarConta(v: String) { _estado.update { it.copy(contaDestinatario = v) } }
    fun atualizarTipoConta(v: String) { _estado.update { it.copy(tipoConta = v) } }
    fun atualizarValor(digits: String) { _estado.update { it.copy(valor = digits) } }
    fun atualizarDescricao(v: String) { _estado.update { it.copy(descricao = v) } }

    fun continuarManual() {
        val s = _estado.value
        if (s.nomeDestinatario.isBlank() || s.bancoDestinatario.isBlank() || s.contaDestinatario.isBlank()) {
            _estado.update { it.copy(mensagemErro = "Preencha os dados do destinatário") }
            return
        }
        _estado.update { it.copy(etapa = TransferenciaEtapa.VALOR) }
    }

    fun avancarParaConfirmacao() {
        val v = _estado.value.valor.toLongOrNull() ?: 0L
        if (v <= 0) {
            _estado.update { it.copy(mensagemErro = "Informe um valor válido") }
            return
        }
        _estado.update { it.copy(etapa = TransferenciaEtapa.CONFIRMACAO) }
    }

    fun confirmarTransferencia() {
        val s = _estado.value
        val valorDouble = (s.valor.toLongOrNull() ?: 0L) / 100.0

        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                val resultado = transferRepository.enviarTransferencia(
                    TransferSendDto(
                        beneficiaryId = s.favorecidoSelecionado?.id,
                        receiverName = s.nomeDestinatario,
                        receiverBank = s.bancoDestinatario,
                        receiverAgency = s.agenciaDestinatario.ifBlank { null },
                        receiverAccount = s.contaDestinatario,
                        receiverAccountType = s.tipoConta,
                        amount = valorDouble,
                        description = s.descricao.ifBlank { null }
                    )
                )
                _estado.update { it.copy(enviando = false, comprovante = resultado, etapa = TransferenciaEtapa.COMPROVANTE) }
            } catch (e: retrofit2.HttpException) {
                val msg = if (e.code() == 400) "Saldo insuficiente" else "Erro ao transferir"
                _estado.update { it.copy(enviando = false, mensagemErro = msg) }
            } catch (e: Exception) {
                _estado.update { it.copy(enviando = false, mensagemErro = "Erro de conexão") }
            }
        }
    }

    fun voltarEtapa() {
        _estado.update {
            when (it.etapa) {
                TransferenciaEtapa.VALOR -> it.copy(etapa = TransferenciaEtapa.DESTINATARIO)
                TransferenciaEtapa.CONFIRMACAO -> it.copy(etapa = TransferenciaEtapa.VALOR)
                else -> it
            }
        }
    }

    fun novaTransferencia() { _estado.update { TransferenciaEnviarUiState() }; carregarFavorecidos() }
    fun limparErro() { _estado.update { it.copy(mensagemErro = null) } }

    fun preCarregarFavorecido(id: String) {
        viewModelScope.launch {
            try {
                val lista = transferRepository.listarFavorecidos()
                lista.find { it.id == id }?.let { selecionarFavorecido(it) }
            } catch (_: Exception) {}
        }
    }
}
