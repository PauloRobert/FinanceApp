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

    // Carrega transações da API REST
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

    // Observa o nome do usuário logado
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
        viewModelScope.launch {
            try {
                val transacao = Transaction(
                    description = descricao,
                    amount = valor,
                    date = data,
                    type = if (ehEntrada) TransactionType.INCOME else TransactionType.EXPENSE
                )
                repositorio.inserirTransacao(transacao)
                carregarTransacoes()
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensagemErro = "Erro ao adicionar transação: ${e.message}"
                )
            }
        }
    }

    fun atualizarTransacao(transacao: Transaction) {
        viewModelScope.launch {
            try {
                repositorio.atualizarTransacao(transacao)
                carregarTransacoes()
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensagemErro = "Erro ao atualizar transação: ${e.message}"
                )
            }
        }
    }

    fun deletarTransacao(transacao: Transaction) {
        viewModelScope.launch {
            try {
                repositorio.deletarTransacao(transacao.id)
                carregarTransacoes()
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    mensagemErro = "Erro ao excluir transação: ${e.message}"
                )
            }
        }
    }

    fun limparErro() {
        _estado.value = _estado.value.copy(mensagemErro = null)
    }
}