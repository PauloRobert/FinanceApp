package com.example.financeapp.domain.repository

import com.example.financeapp.domain.model.ResultadoAuth
import kotlinx.coroutines.flow.Flow

// Contrato de autenticação — abstrai a origem (API REST)
interface AuthRepository {
    suspend fun login(nomeUsuario: String, senha: String): ResultadoAuth
    suspend fun registrar(nomeUsuario: String, senha: String): ResultadoAuth
    suspend fun logout()
    fun estaLogado(): Flow<Boolean>
    fun obterNomeUsuario(): Flow<String?>
}
