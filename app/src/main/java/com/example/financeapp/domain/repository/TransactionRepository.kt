package com.example.financeapp.domain.repository

import com.example.financeapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun obterTransacoes(): Flow<List<Transaction>>
    suspend fun inserirTransacao(transacao: Transaction)
    suspend fun deletarTransacao(id: String)
    suspend fun atualizarTransacao(transacao: Transaction)
}