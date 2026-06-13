package com.example.financeapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.provider.RepositoryProvider
import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime

class HomeViewModel(
    private val repositoryProvider: RepositoryProvider
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeUiState())
    val estado: StateFlow<HomeUiState> = _estado.asStateFlow()

    init {
        observarTransacoes()
        observarOrigem()
    }

    // Observa transações reagindo automaticamente à troca de origem
    private fun observarTransacoes() {
        viewModelScope.launch {
            repositoryProvider.obterTransacoes().collect { lista ->
                _estado.value = _estado.value.copy(transacoes = lista)
            }
        }
    }

    // Observa a origem de dados atual
    private fun observarOrigem() {
        viewModelScope.launch {
            repositoryProvider.origemAtual.collect { origem ->
                _estado.value = _estado.value.copy(origemAtual = origem)
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
            val transacao = Transaction(
                description = descricao,
                amount = valor,
                date = data,
                type = if (ehEntrada) TransactionType.INCOME else TransactionType.EXPENSE
            )
            repositoryProvider.obterRepositorioAtivo().inserirTransacao(transacao)
        }
    }

    fun atualizarTransacao(transacao: Transaction) {
        viewModelScope.launch {
            repositoryProvider.obterRepositorioAtivo().atualizarTransacao(transacao)
        }
    }

    fun deletarTransacao(transacao: Transaction) {
        viewModelScope.launch {
            repositoryProvider.obterRepositorioAtivo().deletarTransacao(transacao.id)
        }
    }
}