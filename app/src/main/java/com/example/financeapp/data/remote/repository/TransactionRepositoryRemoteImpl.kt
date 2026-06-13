package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.LoginRequest
import com.example.financeapp.data.remote.mapper.toDomain
import com.example.financeapp.data.remote.mapper.toRequest
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransactionRepositoryRemoteImpl(
    private val api: TransactionApi,
    private val authInterceptor: AuthInterceptor
) : TransactionRepository {

    // Credenciais padrão para autenticação automática
    private val credenciais = LoginRequest(
        username = "paulo",
        password = "paulo123"
    )

    private var autenticado = false

    // Realiza login e armazena o token no interceptor
    private suspend fun autenticar() {
        if (autenticado) return
        try {
            val resposta = api.login(credenciais)
            authInterceptor.atualizarToken(resposta.accessToken)
            autenticado = true
        } catch (e: Exception) {
            autenticado = false
        }
    }

    // Polling a cada 5 segundos para manter os dados atualizados
    override fun obterTransacoes(): Flow<List<Transaction>> = flow {
        while (true) {
            try {
                autenticar()
                val resposta = api.obterTransacoes()
                val transacoes = resposta.transactions.map { it.toDomain() }
                emit(transacoes)
            } catch (e: Exception) {
                // Em caso de 401, tenta reautenticar na próxima iteração
                autenticado = false
                emit(emptyList())
            }
            delay(5000L)
        }
    }

    override suspend fun inserirTransacao(transacao: Transaction) {
        autenticar()
        api.criarTransacao(transacao.toRequest())
    }

    override suspend fun deletarTransacao(id: String) {
        autenticar()
        api.deletarTransacao(id)
    }

    override suspend fun atualizarTransacao(transacao: Transaction) {
        autenticar()
        api.atualizarTransacao(transacao.id, transacao.toRequest())
    }
}
