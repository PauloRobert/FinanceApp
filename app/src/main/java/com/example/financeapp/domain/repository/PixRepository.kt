package com.example.financeapp.domain.repository

import com.example.financeapp.data.remote.dto.PixDashboardResponse
import com.example.financeapp.data.remote.dto.PixFavoriteDto
import com.example.financeapp.data.remote.dto.PixKeyCreateRequest
import com.example.financeapp.data.remote.dto.PixKeyDto
import com.example.financeapp.data.remote.dto.PixKeyLookupResponse
import com.example.financeapp.data.remote.dto.PixSendRequest
import com.example.financeapp.data.remote.dto.PixTransferDto
import com.example.financeapp.data.remote.dto.PixTransferListResponse

interface PixRepository {
    suspend fun obterDashboard(): PixDashboardResponse
    suspend fun listarChaves(): List<PixKeyDto>
    suspend fun criarChave(dados: PixKeyCreateRequest): PixKeyDto
    suspend fun excluirChave(keyId: String): String
    suspend fun consultarChave(key: String): PixKeyLookupResponse
    suspend fun enviarPix(dados: PixSendRequest): PixTransferDto
    suspend fun obterHistorico(filterType: String = "all"): PixTransferListResponse
    suspend fun obterFavoritos(): List<PixFavoriteDto>
}
