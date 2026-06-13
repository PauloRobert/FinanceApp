package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository

class UpdateTransactionUseCase (private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction){
        repository.updateTransacrion(transaction)
    }
}