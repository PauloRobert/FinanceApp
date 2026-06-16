package com.example.financeapp.ui.screens.pix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.PixSendRequest
import com.example.financeapp.domain.repository.PixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PixEnviarViewModel(
    private val pixRepository: PixRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PixEnviarUiState())
    val estado: StateFlow<PixEnviarUiState> = _estado.asStateFlow()

    fun atualizarChave(valor: String) {
        _estado.update { it.copy(chaveDestino = valor) }
    }

    fun buscarChave() {
        val chave = _estado.value.chaveDestino.trim()
        if (chave.isBlank()) {
            _estado.update { it.copy(mensagemErro = "Informe a chave Pix") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(buscando = true) }
            try {
                val resultado = pixRepository.consultarChave(chave)
                _estado.update {
                    it.copy(
                        buscando = false,
                        destinatario = resultado,
                        etapa = PixEnviarEtapa.VALOR
                    )
                }
            } catch (e: retrofit2.HttpException) {
                _estado.update {
                    it.copy(buscando = false, mensagemErro = "Chave Pix não encontrada")
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(buscando = false, mensagemErro = "Erro de conexão")
                }
            }
        }
    }

    fun atualizarValor(digitos: String) {
        _estado.update { it.copy(valor = digitos) }
    }

    fun atualizarDescricao(valor: String) {
        _estado.update { it.copy(descricao = valor) }
    }

    fun avancarParaConfirmacao() {
        val valorCentavos = _estado.value.valor.toLongOrNull() ?: 0L
        if (valorCentavos <= 0) {
            _estado.update { it.copy(mensagemErro = "Informe um valor válido") }
            return
        }
        _estado.update { it.copy(etapa = PixEnviarEtapa.CONFIRMACAO) }
    }

    fun confirmarPix() {
        val state = _estado.value
        val valorDouble = (state.valor.toLongOrNull() ?: 0L) / 100.0
        if (valorDouble <= 0) return

        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                val resultado = pixRepository.enviarPix(
                    PixSendRequest(
                        receiverKey = state.chaveDestino.trim(),
                        amount = valorDouble,
                        description = state.descricao.ifBlank { null }
                    )
                )
                _estado.update {
                    it.copy(
                        enviando = false,
                        comprovante = resultado,
                        etapa = PixEnviarEtapa.COMPROVANTE,
                        mensagemSucesso = "Pix de R$ ${String.format("%.2f", valorDouble)} enviado com sucesso!"
                    )
                }
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    400 -> "Saldo insuficiente ou limite excedido"
                    404 -> "Chave não encontrada"
                    else -> "Erro ao enviar Pix"
                }
                _estado.update { it.copy(enviando = false, mensagemErro = msg) }
            } catch (e: Exception) {
                _estado.update { it.copy(enviando = false, mensagemErro = "Erro de conexão") }
            }
        }
    }

    fun voltarEtapa() {
        _estado.update {
            when (it.etapa) {
                PixEnviarEtapa.VALOR -> it.copy(etapa = PixEnviarEtapa.BUSCA)
                PixEnviarEtapa.CONFIRMACAO -> it.copy(etapa = PixEnviarEtapa.VALOR)
                else -> it
            }
        }
    }

    fun novoPix() {
        _estado.update { PixEnviarUiState() }
    }

    fun limparErro() {
        _estado.update { it.copy(mensagemErro = null, mensagemSucesso = null) }
    }

    fun definirChaveInicial(chave: String) {
        _estado.update { it.copy(chaveDestino = chave) }
        buscarChave()
    }
}
