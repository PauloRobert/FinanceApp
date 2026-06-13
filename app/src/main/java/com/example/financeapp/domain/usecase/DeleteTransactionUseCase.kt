package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository

class DeleteTransactionUseCase(private val repositorio: TransactionRepository) {
    suspend operator fun invoke(id: String) {
        repositorio.deletarTransacao(id)
    }
}