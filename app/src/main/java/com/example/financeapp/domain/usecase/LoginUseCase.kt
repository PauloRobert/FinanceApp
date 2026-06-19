package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.ResultadoAuth
import com.example.financeapp.domain.repository.AuthRepository

// Caso de uso para realizar login
class LoginUseCase(private val authRepositorio: AuthRepository) {
    suspend operator fun invoke(nomeUsuario: String, senha: String): ResultadoAuth {
        return authRepositorio.login(nomeUsuario, senha)
    }
}
