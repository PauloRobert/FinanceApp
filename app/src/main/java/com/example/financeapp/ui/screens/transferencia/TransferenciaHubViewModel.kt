package com.example.financeapp.ui.screens.transferencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.repository.TransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransferenciaHubViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(TransferenciaHubUiState())
    val estado: StateFlow<TransferenciaHubUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val dashboard = transferRepository.obterDashboard()
                val favorecidos = try { transferRepository.listarFavorecidos() } catch (_: Exception) { emptyList() }
                val historico = try { transferRepository.obterHistorico().transfers } catch (_: Exception) { emptyList() }
                _estado.update {
                    it.copy(
                        carregando = false,
                        saldo = dashboard.balance,
                        limite = dashboard.transferLimit,
                        ultimaData = dashboard.lastTransferDate,
                        ultimoValor = dashboard.lastTransferAmount,
                        totalMes = dashboard.totalTransferredMonth,
                        agendamentos = dashboard.scheduledCount,
                        favorecidos = favorecidos,
                        historico = historico.take(5)
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false, mensagemErro = "Erro ao carregar") }
            }
        }
    }

    fun limparErro() { _estado.update { it.copy(mensagemErro = null) } }
}
