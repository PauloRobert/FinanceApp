package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.PixDashboardResponse
import com.example.financeapp.data.remote.dto.PixFavoriteDto
import com.example.financeapp.data.remote.dto.PixKeyCreateRequest
import com.example.financeapp.data.remote.dto.PixKeyDto
import com.example.financeapp.data.remote.dto.PixKeyLookupResponse
import com.example.financeapp.data.remote.dto.PixSendRequest
import com.example.financeapp.data.remote.dto.PixTransferDto
import com.example.financeapp.data.remote.dto.PixTransferListResponse
import com.example.financeapp.domain.repository.PixRepository
import kotlinx.coroutines.flow.firstOrNull

class PixRepositoryImpl(
    private val api: TransactionApi,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor
) : PixRepository {

    private suspend fun restaurarToken() {
        val token = tokenManager.obterToken().firstOrNull()
        if (!token.isNullOrBlank()) {
            authInterceptor.atualizarToken(token)
        }
    }

    override suspend fun obterDashboard(): PixDashboardResponse {
        restaurarToken()
        return api.obterPixDashboard()
    }

    override suspend fun listarChaves(): List<PixKeyDto> {
        restaurarToken()
        return api.listarChavesPix()
    }

    override suspend fun criarChave(dados: PixKeyCreateRequest): PixKeyDto {
        restaurarToken()
        return api.criarChavePix(dados)
    }

    override suspend fun excluirChave(keyId: String): String {
        restaurarToken()
        return api.excluirChavePix(keyId).message
    }

    override suspend fun consultarChave(key: String): PixKeyLookupResponse {
        restaurarToken()
        return api.consultarChavePix(key)
    }

    override suspend fun enviarPix(dados: PixSendRequest): PixTransferDto {
        restaurarToken()
        return api.enviarPix(dados)
    }

    override suspend fun obterHistorico(filterType: String): PixTransferListResponse {
        restaurarToken()
        return api.obterHistoricoPix(filterType)
    }

    override suspend fun obterFavoritos(): List<PixFavoriteDto> {
        restaurarToken()
        return api.obterFavoritosPix()
    }
}
