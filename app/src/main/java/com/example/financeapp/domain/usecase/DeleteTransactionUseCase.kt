package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository

class DeleteTransactionUseCase (private val repository: TransactionRepository) {
    suspend operator fun invoke(id: String){
        repository.deleteTransacrion(id)
    }
}