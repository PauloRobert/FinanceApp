package com.example.financeapp.domain.repository

import com.example.financeapp.domain.model.OrigemDados
import kotlinx.coroutines.flow.Flow

// Interface para gerenciar a configuração da origem de dados
interface DataSourceConfigRepository {
    fun obterOrigemAtual(): Flow<OrigemDados>
    suspend fun salvarOrigem(origem: OrigemDados)
}

