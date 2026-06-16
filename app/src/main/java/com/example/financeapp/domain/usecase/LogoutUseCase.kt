package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.repository.AuthRepository

// Caso de uso para encerrar sessão
class LogoutUseCase(private val authRepositorio: AuthRepository) {
    suspend operator fun invoke() {
        authRepositorio.logout()
    }
}
