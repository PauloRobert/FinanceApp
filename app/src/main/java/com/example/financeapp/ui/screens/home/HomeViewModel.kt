package com.example.financeapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.provider.RepositoryProvider
import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime

class HomeViewModel(
    private val repositoryProvider: RepositoryProvider,
    private val authRepositorio: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeUiState())
    val estado: StateFlow<HomeUiState> = _estado.asStateFlow()

    init {
        observarTransacoes()
        observarOrigem()
        observarNomeUsuario()
    }

    // Observa transações reagindo automaticamente à troca de origem
    private fun observarTransacoes() {
        viewModelScope.launch {
            repositoryProvider.obterTransacoes()
                .onStart {
                    _estado.value = _estado.value.copy(carregando = true)
                }
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

    // Observa a origem de dados atual
    private fun observarOrigem() {
        viewModelScope.launch {
            repositoryProvider.origemAtual.collect { origem ->
                _estado.value = _estado.value.copy(origemAtual = origem)
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
                repositoryProvider.obterRepositorioAtivo().inserirTransacao(transacao)
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
                repositoryProvider.obterRepositorioAtivo().atualizarTransacao(transacao)
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
                repositoryProvider.obterRepositorioAtivo().deletarTransacao(transacao.id)
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