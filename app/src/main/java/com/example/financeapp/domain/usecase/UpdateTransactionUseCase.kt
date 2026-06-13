package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository

class UpdateTransactionUseCase(private val repositorio: TransactionRepository) {
    suspend operator fun invoke(transacao: Transaction) {
        repositorio.atualizarTransacao(transacao)
    }
}