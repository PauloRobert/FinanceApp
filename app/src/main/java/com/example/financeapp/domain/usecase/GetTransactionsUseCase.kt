package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase ( private val repository: TransactionRepository) {
    suspend operator fun invoke(): Flow<List<Transaction>>{
        return repository.getTransactions()
    }
}