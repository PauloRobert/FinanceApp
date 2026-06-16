package com.example.financeapp.domain.repository

import com.example.financeapp.data.remote.dto.PasswordChangeRequest
import com.example.financeapp.data.remote.dto.PrivacyResponse
import com.example.financeapp.data.remote.dto.PrivacyUpdateRequest
import com.example.financeapp.data.remote.dto.ProfileResponse
import com.example.financeapp.data.remote.dto.ProfileUpdateRequest
import com.example.financeapp.data.remote.dto.SecurityResponse
import com.example.financeapp.data.remote.dto.SecurityUpdateRequest

interface UserRepository {
    suspend fun obterPerfil(): ProfileResponse
    suspend fun atualizarPerfil(dados: ProfileUpdateRequest): ProfileResponse
    suspend fun obterSeguranca(): SecurityResponse
    suspend fun atualizarSeguranca(dados: SecurityUpdateRequest): SecurityResponse
    suspend fun alterarSenha(dados: PasswordChangeRequest): String
    suspend fun obterPrivacidade(): PrivacyResponse
    suspend fun atualizarPrivacidade(dados: PrivacyUpdateRequest): PrivacyResponse
}
