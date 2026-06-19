package com.example.financeapp.domain.repository

import com.example.financeapp.data.remote.dto.BeneficiaryCreateRequest
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.TransferDashboardDto
import com.example.financeapp.data.remote.dto.TransferListDto
import com.example.financeapp.data.remote.dto.TransferResponseDto
import com.example.financeapp.data.remote.dto.TransferSendDto

interface TransferRepository {
    suspend fun obterDashboard(): TransferDashboardDto
    suspend fun listarFavorecidos(search: String = ""): List<BeneficiaryDto>
    suspend fun criarFavorecido(dados: BeneficiaryCreateRequest): BeneficiaryDto
    suspend fun atualizarFavorecido(id: String, dados: BeneficiaryCreateRequest): BeneficiaryDto
    suspend fun excluirFavorecido(id: String): String
    suspend fun enviarTransferencia(dados: TransferSendDto): TransferResponseDto
    suspend fun obterHistorico(): TransferListDto
}
