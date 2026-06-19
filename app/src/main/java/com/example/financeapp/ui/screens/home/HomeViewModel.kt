package com.example.financeapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.AuthRepository
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime

class HomeViewModel(
    private val repositorio: TransactionRepository,
    private val authRepositorio: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeUiState())
    val estado: StateFlow<HomeUiState> = _estado.asStateFlow()

    init {
        carregarTransacoes()
        observarNomeUsuario()
    }

    // Carrega transações da API (apenas no init ou pull-to-refresh)
    fun carregarTransacoes() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(carregando = true)
            repositorio.obterTransacoes()
                .catch { erro ->
                    _estado.value = _estado.value.copy(
                        carregando = false,
                        mensagemErro = "Erro ao carregar transações: ${erro.message}"
                    )
                }
                .collect { lista ->
                    _estado.value = _estado.value.copy(
                        transacoes = lista,
                        carregando = false
                    )
                }
        }
    }

    // Sync silencioso — atualiza lista sem mostrar loading
    private fun sincronizarSilenciosamente() {
        viewModelScope.launch {
            repositorio.obterTransacoes()
                .catch { /* silencioso */ }
                .collect { lista ->
                    _estado.value = _estado.value.copy(transacoes = lista)
                }
        }
    }

    private fun observarNomeUsuario() {
        viewModelScope.launch {
            authRepositorio.obterNomeUsuario().collect { nome ->
                _estado.value = _estado.value.copy(nomeUsuario = nome ?: "")
            }
        }
    }

    fun adicionarTransacao(
        descricao: String,
        valor: BigDecimal,
        data: LocalDateTime,
        ehEntrada: Boolean
    ) {
        val transacao = Transaction(
            id = "temp_${System.currentTimeMillis()}",
            description = descricao,
            amount = valor,
            date = data,
            type = if (ehEntrada) TransactionType.INCOME else TransactionType.EXPENSE
        )

        // Optimistic update — adiciona na lista local imediatamente
        _estado.value = _estado.value.copy(
            transacoes = listOf(transacao) + _estado.value.transacoes
        )

        viewModelScope.launch {
            try {
                repositorio.inserirTransacao(transacao)
                sincronizarSilenciosamente()
            } catch (e: Exception) {
                // Rollback — remove a transação temporária
                _estado.value = _estado.value.copy(
                    transacoes = _estado.value.transacoes.filter { it.id != transacao.id },
                    mensagemErro = "Erro ao adicionar transação: ${e.message}"
                )
            }
        }
    }

    fun atualizarTransacao(transacao: Transaction) {
        // Optimistic update — substitui na lista local
        _estado.value = _estado.value.copy(
            transacoes = _estado.value.transacoes.map {
                if (it.id == transacao.id) transacao else it
            }
        )

        viewModelScope.launch {
            try {
                repositorio.atualizarTransacao(transacao)
                sincronizarSilenciosamente()
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensagemErro = "Erro ao atualizar transação: ${e.message}"
                )
                sincronizarSilenciosamente() // Reverte ao estado real
            }
        }
    }

    fun deletarTransacao(transacao: Transaction) {
        // Optimistic update — remove da lista local
        _estado.value = _estado.value.copy(
            transacoes = _estado.value.transacoes.filter { it.id != transacao.id }
        )

        viewModelScope.launch {
            try {
                repositorio.deletarTransacao(transacao.id)
                sincronizarSilenciosamente()
            } catch (e: Exception) {
                // Rollback — re-adiciona a transação
                _estado.value = _estado.value.copy(
                    transacoes = _estado.value.transacoes + transacao,
                    mensagemErro = "Erro ao excluir transação: ${e.message}"
                )
            }
        }
    }

    fun limparErro() {
        _estado.value = _estado.value.copy(mensagemErro = null)
    }
}