package com.example.financeapp.data.sync

import android.util.Log
import com.example.financeapp.data.connectivity.ConnectivityObserver
import com.example.financeapp.data.room.dao.TransactionDao
import com.example.financeapp.data.room.mapper.toDomain
import com.example.financeapp.data.room.mapper.toEntity
import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.repository.DataSourceConfigRepository
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Gerenciador de sincronização offline-first
class SyncManager(
    private val syncDao: SyncDao,
    private val transactionDao: TransactionDao,
    private val connectivityObserver: ConnectivityObserver,
    private val configRepositorio: DataSourceConfigRepository,
    private val repositorioRemote: TransactionRepository,
    private val repositorioFirebase: TransactionRepository
) {

    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "SyncManager"

    // Estado de sincronização observável
    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    private val _pendentes = MutableStateFlow(0)
    val pendentes: StateFlow<Int> = _pendentes.asStateFlow()

    init {
        // Observar conectividade e sincronizar quando voltar online
        escopo.launch {
            connectivityObserver.observar().collect { conectado ->
                if (conectado) {
                    delay(2000) // Aguardar estabilização da rede
                    sincronizar()
                }
            }
        }

        // Sincronização periódica a cada 30 segundos
        escopo.launch {
            while (true) {
                delay(30_000)
                if (connectivityObserver.estaConectado()) {
                    sincronizar()
                }
                atualizarContadorPendentes()
            }
        }
    }

    // Registra uma operação para sincronização futura
    suspend fun registrarOperacao(transacaoId: String, tipo: TipoOperacao) {
        syncDao.inserirOperacao(
            OperacaoPendente(
                transacaoId = transacaoId,
                tipo = tipo.name
            )
        )
        atualizarContadorPendentes()

        // Tenta sincronizar imediatamente se online
        if (connectivityObserver.estaConectado()) {
            sincronizar()
        }
    }

    // Executa sincronização de operações pendentes
    suspend fun sincronizar() {
        if (_sincronizando.value) return

        _sincronizando.value = true
        try {
            val origem = configRepositorio.obterOrigemAtual().first()
            val repositorioNuvem = obterRepositorioNuvem(origem) ?: return

            val operacoes = syncDao.obterPendentes()
            if (operacoes.isEmpty()) {
                // Sem operações pendentes, puxar dados da nuvem
                puxarDadosNuvem(repositorioNuvem)
                return
            }

            Log.d(TAG, "Sincronizando ${operacoes.size} operações pendentes")

            for (operacao in operacoes) {
                try {
                    executarOperacao(operacao, repositorioNuvem)
                    syncDao.removerOperacao(operacao.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao sincronizar operação ${operacao.id}: ${e.message}")
                    // Mantém a operação na fila para tentar novamente
                }
            }

            // Após enviar, puxar dados atualizados da nuvem
            puxarDadosNuvem(repositorioNuvem)

        } catch (e: Exception) {
            Log.e(TAG, "Erro durante sincronização: ${e.message}")
        } finally {
            _sincronizando.value = false
            atualizarContadorPendentes()
        }
    }

    // Executa uma operação individual na nuvem
    private suspend fun executarOperacao(
        operacao: OperacaoPendente,
        repositorioNuvem: TransactionRepository
    ) {
        when (TipoOperacao.valueOf(operacao.tipo)) {
            TipoOperacao.INSERIR, TipoOperacao.ATUALIZAR -> {
                val entity = transactionDao.obterPorId(operacao.transacaoId)
                if (entity != null) {
                    val transacao = entity.toDomain()
                    if (TipoOperacao.valueOf(operacao.tipo) == TipoOperacao.INSERIR) {
                        repositorioNuvem.inserirTransacao(transacao)
                    } else {
                        repositorioNuvem.atualizarTransacao(transacao)
                    }
                }
            }

            TipoOperacao.DELETAR -> {
                repositorioNuvem.deletarTransacao(operacao.transacaoId)
            }
        }
    }

    // Puxa dados da nuvem e atualiza o cache local
    private suspend fun puxarDadosNuvem(repositorioNuvem: TransactionRepository) {
        try {
            val transacoesNuvem = repositorioNuvem.obterTransacoes().first()
            // Atualizar cache local com dados da nuvem
            for (transacao in transacoesNuvem) {
                transactionDao.inserir(transacao.toEntity())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao puxar dados da nuvem: ${e.message}")
        }
    }

    // Retorna o repositório de nuvem conforme a origem configurada
    private fun obterRepositorioNuvem(origem: OrigemDados): TransactionRepository? {
        return when (origem) {
            OrigemDados.REMOTE -> repositorioRemote
            OrigemDados.FIREBASE -> repositorioFirebase
            OrigemDados.ROOM -> null // Room não precisa sincronizar
        }
    }

    private suspend fun atualizarContadorPendentes() {
        _pendentes.value = syncDao.contarPendentes()
    }
}

