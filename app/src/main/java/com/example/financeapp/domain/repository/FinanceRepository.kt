package com.example.financeapp.domain.repository

import com.example.financeapp.data.remote.dto.*

interface FinanceRepository {
    // Deposits
    suspend fun criarDeposito(dados: DepositRequestDto): DepositDto
    suspend fun listarDepositos(): DepositListDto
    // Payments
    suspend fun criarPagamento(dados: PaymentRequestDto): PaymentDto
    suspend fun listarPagamentos(): PaymentListDto
    // Cards
    suspend fun listarCartoes(): List<CardDto>
    suspend fun criarCartao(dados: CardCreateDto): CardDto
    suspend fun atualizarCartao(id: String, dados: CardUpdateDto): CardDto
    suspend fun cancelarCartao(id: String): String
    suspend fun listarTransacoesCartao(id: String): CardTransactionListDto
    // Investments
    suspend fun obterInvestmentDashboard(): InvestmentDashboardDto
    suspend fun listarInvestimentos(): InvestmentListDto
    suspend fun criarInvestimento(dados: InvestmentCreateDto): InvestmentDto
    suspend fun resgatarInvestimento(id: String): InvestmentDto
}
