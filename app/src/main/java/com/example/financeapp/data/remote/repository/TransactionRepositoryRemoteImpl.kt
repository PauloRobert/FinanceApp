package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.mapper.toDomain
import com.example.financeapp.data.remote.mapper.toRequest
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransactionRepositoryRemoteImpl(
    private val api: TransactionApi
) : TransactionRepository {

    // Polling a cada 5 segundos para manter os dados atualizados
    override fun obterTransacoes(): Flow<List<Transaction>> = flow {
        while (true) {
            try {
                val resposta = api.obterTransacoes()
                val transacoes = resposta.transactions.map { it.toDomain() }
                emit(transacoes)
            } catch (e: Exception) {
                // Em caso de erro, emite lista vazia
                emit(emptyList())
            }
            delay(5000L)
        }
    }

    override suspend fun inserirTransacao(transacao: Transaction) {
        api.criarTransacao(transacao.toRequest())
    }

    override suspend fun deletarTransacao(id: String) {
        api.deletarTransacao(id)
    }

    override suspend fun atualizarTransacao(transacao: Transaction) {
        api.atualizarTransacao(transacao.id, transacao.toRequest())
    }
}

