package com.example.financeapp.ui.screens.privacidade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.PrivacyUpdateRequest
import com.example.financeapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrivacidadeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PrivacidadeUiState())
    val estado: StateFlow<PrivacidadeUiState> = _estado.asStateFlow()

    init {
        carregarPrivacidade()
    }

    private fun carregarPrivacidade() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val privacidade = userRepository.obterPrivacidade()
                _estado.update {
                    privacidade.toUiState().copy(carregando = false)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(carregando = false, mensagemErro = "Erro ao carregar configurações")
                }
            }
        }
    }

    fun alternarCompartilharDados(valor: Boolean) {
        _estado.update { it.copy(shareUsageData = valor) }
        atualizarPrivacidade(PrivacyUpdateRequest(shareUsageData = valor))
    }

    fun alternarMostrarSaldo(valor: Boolean) {
        _estado.update { it.copy(showBalanceOnHome = valor) }
        atualizarPrivacidade(PrivacyUpdateRequest(showBalanceOnHome = valor))
    }

    fun alternarNotificacoesTransacao(valor: Boolean) {
        _estado.update { it.copy(transactionNotifications = valor) }
        atualizarPrivacidade(PrivacyUpdateRequest(transactionNotifications = valor))
    }

    fun alternarEmailsMarketing(valor: Boolean) {
        _estado.update { it.copy(marketingEmails = valor) }
        atualizarPrivacidade(PrivacyUpdateRequest(marketingEmails = valor))
    }

    private fun atualizarPrivacidade(dados: PrivacyUpdateRequest) {
        viewModelScope.launch {
            try {
                userRepository.atualizarPrivacidade(dados)
            } catch (e: Exception) {
                _estado.update { it.copy(mensagemErro = "Erro ao atualizar") }
            }
        }
    }

    fun limparMensagens() {
        _estado.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }
}
