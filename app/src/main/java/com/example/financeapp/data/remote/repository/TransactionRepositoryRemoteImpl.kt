package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.mapper.toDomain
import com.example.financeapp.data.remote.mapper.toRequest
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TransactionRepositoryRemoteImpl(
    private val api: TransactionApi,
    private val authInterceptor: AuthInterceptor,
    private val tokenManager: TokenManager
) : TransactionRepository {

    // Restaura o token do DataStore no interceptor antes de cada chamada
    private suspend fun garantirToken() {
        val token = tokenManager.obterToken().first()
        if (!token.isNullOrBlank()) {
            authInterceptor.atualizarToken(token)
        }
    }

    override fun obterTransacoes(): Flow<List<Transaction>> = flow {
        garantirToken()
        try {
            val resposta = api.obterTransacoes()
            val transacoes = resposta.transactions.map { it.toDomain() }
            emit(transacoes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun inserirTransacao(transacao: Transaction) {
        garantirToken()
        api.criarTransacao(transacao.toRequest())
    }

    override suspend fun deletarTransacao(id: String) {
        garantirToken()
        api.deletarTransacao(id)
    }

    override suspend fun atualizarTransacao(transacao: Transaction) {
        garantirToken()
        api.atualizarTransacao(transacao.id, transacao.toRequest())
    }
}
