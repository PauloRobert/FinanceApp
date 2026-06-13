package com.example.financeapp.data.sync

import com.example.financeapp.data.room.dao.TransactionDao
import com.example.financeapp.data.room.mapper.toDomain
import com.example.financeapp.data.room.mapper.toEntity
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

// Repositório Offline First: grava em Room e agenda sincronização com a nuvem
class TransactionRepositoryOfflineFirst(
    private val dao: TransactionDao,
    private val syncManager: SyncManager
) : TransactionRepository {

    // Sempre lê do cache local (Room)
    override fun obterTransacoes(): Flow<List<Transaction>> {
        return dao.obterTodas().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    // Grava localmente e agenda sync
    override suspend fun inserirTransacao(transacao: Transaction) {
        val transacaoComId = if (transacao.id.isBlank()) {
            transacao.copy(id = UUID.randomUUID().toString())
        } else {
            transacao
        }
        dao.inserir(transacaoComId.toEntity())
        syncManager.registrarOperacao(transacaoComId.id, TipoOperacao.INSERIR)
    }

    // Deleta localmente e agenda sync
    override suspend fun deletarTransacao(id: String) {
        dao.deletar(id)
        syncManager.registrarOperacao(id, TipoOperacao.DELETAR)
    }

    // Atualiza localmente e agenda sync
    override suspend fun atualizarTransacao(transacao: Transaction) {
        dao.atualizar(transacao.toEntity())
        syncManager.registrarOperacao(transacao.id, TipoOperacao.ATUALIZAR)
    }
}

