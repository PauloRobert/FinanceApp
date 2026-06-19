package com.example.financeapp.domain.usecase

import com.example.financeapp.domain.model.ResultadoAuth
import com.example.financeapp.domain.repository.AuthRepository

// Caso de uso para registrar novo usuário
class RegistrarUseCase(private val authRepositorio: AuthRepository) {
    suspend operator fun invoke(nomeUsuario: String, senha: String): ResultadoAuth {
        return authRepositorio.registrar(nomeUsuario, senha)
    }
}
