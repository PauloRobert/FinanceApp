package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.PasswordChangeRequest
import com.example.financeapp.data.remote.dto.PrivacyResponse
import com.example.financeapp.data.remote.dto.PrivacyUpdateRequest
import com.example.financeapp.data.remote.dto.ProfileResponse
import com.example.financeapp.data.remote.dto.ProfileUpdateRequest
import com.example.financeapp.data.remote.dto.SecurityResponse
import com.example.financeapp.data.remote.dto.SecurityUpdateRequest
import com.example.financeapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl(
    private val api: TransactionApi,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor
) : UserRepository {

    private suspend fun restaurarToken() {
        val token = tokenManager.obterToken().firstOrNull()
        if (!token.isNullOrBlank()) {
            authInterceptor.atualizarToken(token)
        }
    }

    override suspend fun obterPerfil(): ProfileResponse {
        restaurarToken()
        return api.obterPerfil()
    }

    override suspend fun atualizarPerfil(dados: ProfileUpdateRequest): ProfileResponse {
        restaurarToken()
        return api.atualizarPerfil(dados)
    }

    override suspend fun obterSeguranca(): SecurityResponse {
        restaurarToken()
        return api.obterSeguranca()
    }

    override suspend fun atualizarSeguranca(dados: SecurityUpdateRequest): SecurityResponse {
        restaurarToken()
        return api.atualizarSeguranca(dados)
    }

    override suspend fun alterarSenha(dados: PasswordChangeRequest): String {
        restaurarToken()
        return api.alterarSenha(dados).message
    }

    override suspend fun obterPrivacidade(): PrivacyResponse {
        restaurarToken()
        return api.obterPrivacidade()
    }

    override suspend fun atualizarPrivacidade(dados: PrivacyUpdateRequest): PrivacyResponse {
        restaurarToken()
        return api.atualizarPrivacidade(dados)
    }
}
