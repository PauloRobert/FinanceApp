package com.example.financeapp.domain.repository

import com.example.financeapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transactions: Transaction)
    suspend fun deleteTransacrion(id: String)
    suspend fun updateTransacrion(transactions: Transaction)
}