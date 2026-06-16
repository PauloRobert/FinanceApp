package com.example.financeapp.ui.screens.pix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.repository.PixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PixHubViewModel(
    private val pixRepository: PixRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PixHubUiState())
    val estado: StateFlow<PixHubUiState> = _estado.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val dashboard = pixRepository.obterDashboard()
                val favoritos = try { pixRepository.obterFavoritos() } catch (_: Exception) { emptyList() }
                _estado.update {
                    it.copy(
                        carregando = false,
                        saldo = dashboard.balance,
                        limiteDiario = dashboard.dailyLimit,
                        limiteUsado = dashboard.dailyUsed,
                        pixAtivo = dashboard.pixActive,
                        totalEnviadoMes = dashboard.totalSentMonth,
                        totalRecebidoMes = dashboard.totalReceivedMonth,
                        qtdChaves = dashboard.keysCount,
                        favoritos = favoritos
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false, mensagemErro = "Erro ao carregar") }
            }
        }
    }

    fun limparErro() {
        _estado.update { it.copy(mensagemErro = null) }
    }
}
