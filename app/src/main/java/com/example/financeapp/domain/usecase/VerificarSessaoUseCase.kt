package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

// Caso de uso para verificar se há sessão ativa
class VerificarSessaoUseCase(private val authRepositorio: AuthRepository) {
    operator fun invoke(): Flow<Boolean> {
        return authRepositorio.estaLogado()
    }
}
