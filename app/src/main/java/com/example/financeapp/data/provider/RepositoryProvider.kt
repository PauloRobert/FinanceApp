package com.example.financeapp.data.provider

import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.DataSourceConfigRepository
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

// Provedor que gerencia a troca dinâmica do repositório ativo
class RepositoryProvider(
    private val configRepositorio: DataSourceConfigRepository,
    private val repositorioRoom: TransactionRepository,
    private val repositorioRemote: TransactionRepository,
    private val repositorioFirebase: TransactionRepository
) {

    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _origemAtual = MutableStateFlow(OrigemDados.FIREBASE)
    val origemAtual: StateFlow<OrigemDados> = _origemAtual.asStateFlow()

    init {
        escopo.launch {
            configRepositorio.obterOrigemAtual().collect { origem ->
                _origemAtual.value = origem
            }
        }
    }

    // Retorna o repositório correspondente à origem atual
    fun obterRepositorioAtivo(): TransactionRepository {
        return when (_origemAtual.value) {
            OrigemDados.ROOM -> repositorioRoom
            OrigemDados.REMOTE -> repositorioRemote
            OrigemDados.FIREBASE -> repositorioFirebase
        }
    }

    // Flow de transações que reage automaticamente à troca de origem
    @OptIn(ExperimentalCoroutinesApi::class)
    fun obterTransacoes(): Flow<List<Transaction>> {
        return _origemAtual.flatMapLatest { origem ->
            when (origem) {
                OrigemDados.ROOM -> repositorioRoom.obterTransacoes()
                OrigemDados.REMOTE -> repositorioRemote.obterTransacoes()
                OrigemDados.FIREBASE -> repositorioFirebase.obterTransacoes()
            }
        }
    }

    suspend fun trocarOrigem(novaOrigem: OrigemDados) {
        configRepositorio.salvarOrigem(novaOrigem)
    }
}
