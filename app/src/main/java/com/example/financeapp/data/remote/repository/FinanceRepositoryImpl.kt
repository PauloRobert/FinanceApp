package com.example.financeapp.data.remote.repository

import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.*
import com.example.financeapp.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.firstOrNull

class FinanceRepositoryImpl(
    private val api: TransactionApi,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor
) : FinanceRepository {

    private suspend fun auth() {
        val t = tokenManager.obterToken().firstOrNull()
        if (!t.isNullOrBlank()) authInterceptor.atualizarToken(t)
    }

    override suspend fun criarDeposito(dados: DepositRequestDto): DepositDto { auth(); return api.criarDeposito(dados) }
    override suspend fun listarDepositos(): DepositListDto { auth(); return api.listarDepositos() }
    override suspend fun criarPagamento(dados: PaymentRequestDto): PaymentDto { auth(); return api.criarPagamento(dados) }
    override suspend fun listarPagamentos(): PaymentListDto { auth(); return api.listarPagamentos() }
    override suspend fun listarCartoes(): List<CardDto> { auth(); return api.listarCartoes() }
    override suspend fun criarCartao(dados: CardCreateDto): CardDto { auth(); return api.criarCartao(dados) }
    override suspend fun atualizarCartao(id: String, dados: CardUpdateDto): CardDto { auth(); return api.atualizarCartao(id, dados) }
    override suspend fun cancelarCartao(id: String): String { auth(); return api.cancelarCartao(id).message }
    override suspend fun listarTransacoesCartao(id: String): CardTransactionListDto { auth(); return api.listarTransacoesCartao(id) }
    override suspend fun obterInvestmentDashboard(): InvestmentDashboardDto { auth(); return api.obterInvestmentDashboard() }
    override suspend fun listarInvestimentos(): InvestmentListDto { auth(); return api.listarInvestimentos() }
    override suspend fun criarInvestimento(dados: InvestmentCreateDto): InvestmentDto { auth(); return api.criarInvestimento(dados) }
    override suspend fun resgatarInvestimento(id: String): InvestmentDto { auth(); return api.resgatarInvestimento(id) }
}
