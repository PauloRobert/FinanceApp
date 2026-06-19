package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.BeneficiaryCreateRequest
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.TransferDashboardDto
import com.example.financeapp.data.remote.dto.TransferListDto
import com.example.financeapp.data.remote.dto.TransferResponseDto
import com.example.financeapp.data.remote.dto.TransferSendDto
import com.example.financeapp.domain.repository.TransferRepository
import kotlinx.coroutines.flow.firstOrNull

class TransferRepositoryImpl(
    private val api: TransactionApi,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor
) : TransferRepository {

    private suspend fun restaurarToken() {
        val token = tokenManager.obterToken().firstOrNull()
        if (!token.isNullOrBlank()) authInterceptor.atualizarToken(token)
    }

    override suspend fun obterDashboard(): TransferDashboardDto {
        restaurarToken(); return api.obterTransferDashboard()
    }

    override suspend fun listarFavorecidos(search: String): List<BeneficiaryDto> {
        restaurarToken(); return api.listarFavorecidos(search)
    }

    override suspend fun criarFavorecido(dados: BeneficiaryCreateRequest): BeneficiaryDto {
        restaurarToken(); return api.criarFavorecido(dados)
    }

    override suspend fun atualizarFavorecido(id: String, dados: BeneficiaryCreateRequest): BeneficiaryDto {
        restaurarToken(); return api.atualizarFavorecido(id, dados)
    }

    override suspend fun excluirFavorecido(id: String): String {
        restaurarToken(); return api.excluirFavorecido(id).message
    }

    override suspend fun enviarTransferencia(dados: TransferSendDto): TransferResponseDto {
        restaurarToken(); return api.enviarTransferencia(dados)
    }

    override suspend fun obterHistorico(): TransferListDto {
        restaurarToken(); return api.obterHistoricoTransferencias()
    }
}
