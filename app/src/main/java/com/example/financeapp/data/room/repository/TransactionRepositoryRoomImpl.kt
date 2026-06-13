package com.example.financeapp.data.room.repository

import com.example.financeapp.data.room.dao.TransactionDao
import com.example.financeapp.data.room.mapper.toDomain
import com.example.financeapp.data.room.mapper.toEntity
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TransactionRepositoryRoomImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun obterTransacoes(): Flow<List<Transaction>> {
        return dao.obterTodas().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun inserirTransacao(transacao: Transaction) {
        val transacaoComId = if (transacao.id.isBlank()) {
            transacao.copy(id = UUID.randomUUID().toString())
        } else {
            transacao
        }
        dao.inserir(transacaoComId.toEntity())
    }

    override suspend fun deletarTransacao(id: String) {
        dao.deletar(id)
    }

    override suspend fun atualizarTransacao(transacao: Transaction) {
        dao.atualizar(transacao.toEntity())
    }
}

