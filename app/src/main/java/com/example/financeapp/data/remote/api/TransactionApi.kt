package com.example.financeapp.data.remote.api

import com.example.financeapp.data.remote.dto.BeneficiaryCreateRequest
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.data.remote.dto.CardCreateDto
import com.example.financeapp.data.remote.dto.CardDto
import com.example.financeapp.data.remote.dto.CardTransactionListDto
import com.example.financeapp.data.remote.dto.CardUpdateDto
import com.example.financeapp.data.remote.dto.DepositDto
import com.example.financeapp.data.remote.dto.DepositListDto
import com.example.financeapp.data.remote.dto.DepositRequestDto
import com.example.financeapp.data.remote.dto.InvestmentCreateDto
import com.example.financeapp.data.remote.dto.InvestmentDashboardDto
import com.example.financeapp.data.remote.dto.InvestmentDto
import com.example.financeapp.data.remote.dto.InvestmentListDto
import com.example.financeapp.data.remote.dto.LoginRequest
import com.example.financeapp.data.remote.dto.MessageResponse
import com.example.financeapp.data.remote.dto.StockListDto
import com.example.financeapp.data.remote.dto.PaymentDto
import com.example.financeapp.data.remote.dto.PaymentListDto
import com.example.financeapp.data.remote.dto.PaymentRequestDto
import com.example.financeapp.data.remote.dto.PasswordChangeRequest
import com.example.financeapp.data.remote.dto.PrivacyResponse
import com.example.financeapp.data.remote.dto.PrivacyUpdateRequest
import com.example.financeapp.data.remote.dto.PixDashboardResponse
import com.example.financeapp.data.remote.dto.PixFavoriteDto
import com.example.financeapp.data.remote.dto.PixKeyCreateRequest
import com.example.financeapp.data.remote.dto.PixKeyDto
import com.example.financeapp.data.remote.dto.PixKeyLookupResponse
import com.example.financeapp.data.remote.dto.PixSendRequest
import com.example.financeapp.data.remote.dto.PixTransferDto
import com.example.financeapp.data.remote.dto.PixTransferListResponse
import com.example.financeapp.data.remote.dto.ProfileResponse
import com.example.financeapp.data.remote.dto.ProfileUpdateRequest
import com.example.financeapp.data.remote.dto.RegisterRequest
import com.example.financeapp.data.remote.dto.SecurityResponse
import com.example.financeapp.data.remote.dto.SecurityUpdateRequest
import com.example.financeapp.data.remote.dto.TokenResponse
import com.example.financeapp.data.remote.dto.TransactionListResponse
import com.example.financeapp.data.remote.dto.TransactionRemoteDto
import com.example.financeapp.data.remote.dto.TransactionRequest
import com.example.financeapp.data.remote.dto.TransferDashboardDto
import com.example.financeapp.data.remote.dto.TransferListDto
import com.example.financeapp.data.remote.dto.TransferResponseDto
import com.example.financeapp.data.remote.dto.TransferSendDto
import com.example.financeapp.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApi {

    // ── Auth ────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body credenciais: LoginRequest): TokenResponse

    @POST("auth/register")
    suspend fun registrar(@Body dados: RegisterRequest): UserResponse

    // ── Transactions ────────────────────────────────────
    @GET("transactions")
    suspend fun obterTransacoes(): TransactionListResponse

    @POST("transactions")
    suspend fun criarTransacao(@Body transacao: TransactionRequest): TransactionRemoteDto

    @PUT("transactions/{id}")
    suspend fun atualizarTransacao(
        @Path("id") id: String,
        @Body transacao: TransactionRequest
    ): TransactionRemoteDto

    @DELETE("transactions/{id}")
    suspend fun deletarTransacao(@Path("id") id: String)

    // ── User Profile ────────────────────────────────────
    @GET("user/profile")
    suspend fun obterPerfil(): ProfileResponse

    @PUT("user/profile")
    suspend fun atualizarPerfil(@Body dados: ProfileUpdateRequest): ProfileResponse

    // ── User Security ───────────────────────────────────
    @GET("user/security")
    suspend fun obterSeguranca(): SecurityResponse

    @PUT("user/security")
    suspend fun atualizarSeguranca(@Body dados: SecurityUpdateRequest): SecurityResponse

    @POST("user/security/change-password")
    suspend fun alterarSenha(@Body dados: PasswordChangeRequest): MessageResponse

    // ── User Privacy ────────────────────────────────────
    @GET("user/privacy")
    suspend fun obterPrivacidade(): PrivacyResponse

    @PUT("user/privacy")
    suspend fun atualizarPrivacidade(@Body dados: PrivacyUpdateRequest): PrivacyResponse

    // ── Pix ─────────────────────────────────────────────
    @GET("pix/dashboard")
    suspend fun obterPixDashboard(): PixDashboardResponse

    @GET("pix/keys")
    suspend fun listarChavesPix(): List<PixKeyDto>

    @POST("pix/keys")
    suspend fun criarChavePix(@Body dados: PixKeyCreateRequest): PixKeyDto

    @DELETE("pix/keys/{keyId}")
    suspend fun excluirChavePix(@Path("keyId") keyId: String): MessageResponse

    @GET("pix/lookup")
    suspend fun consultarChavePix(@Query("key") key: String): PixKeyLookupResponse

    @POST("pix/send")
    suspend fun enviarPix(@Body dados: PixSendRequest): PixTransferDto

    @GET("pix/history")
    suspend fun obterHistoricoPix(@Query("filter_type") filterType: String = "all"): PixTransferListResponse

    @GET("pix/favorites")
    suspend fun obterFavoritosPix(): List<PixFavoriteDto>

    // ── Transfers ───────────────────────────────────────
    @GET("transfers/dashboard")
    suspend fun obterTransferDashboard(): TransferDashboardDto

    @GET("transfers/beneficiaries")
    suspend fun listarFavorecidos(@Query("search") search: String = ""): List<BeneficiaryDto>

    @POST("transfers/beneficiaries")
    suspend fun criarFavorecido(@Body dados: BeneficiaryCreateRequest): BeneficiaryDto

    @PUT("transfers/beneficiaries/{id}")
    suspend fun atualizarFavorecido(@Path("id") id: String, @Body dados: BeneficiaryCreateRequest): BeneficiaryDto

    @DELETE("transfers/beneficiaries/{id}")
    suspend fun excluirFavorecido(@Path("id") id: String): MessageResponse

    @POST("transfers/send")
    suspend fun enviarTransferencia(@Body dados: TransferSendDto): TransferResponseDto

    @GET("transfers/history")
    suspend fun obterHistoricoTransferencias(): TransferListDto

    // ── Deposits ────────────────────────────────────────
    @POST("deposits/")
    suspend fun criarDeposito(@Body dados: DepositRequestDto): DepositDto
    @GET("deposits/")
    suspend fun listarDepositos(): DepositListDto

    // ── Payments ────────────────────────────────────────
    @POST("payments/")
    suspend fun criarPagamento(@Body dados: PaymentRequestDto): PaymentDto
    @GET("payments/")
    suspend fun listarPagamentos(): PaymentListDto

    // ── Cards ───────────────────────────────────────────
    @GET("cards/")
    suspend fun listarCartoes(): List<CardDto>
    @POST("cards/")
    suspend fun criarCartao(@Body dados: CardCreateDto): CardDto
    @PUT("cards/{id}")
    suspend fun atualizarCartao(@Path("id") id: String, @Body dados: CardUpdateDto): CardDto
    @DELETE("cards/{id}")
    suspend fun cancelarCartao(@Path("id") id: String): MessageResponse
    @GET("cards/{id}/transactions")
    suspend fun listarTransacoesCartao(@Path("id") id: String): CardTransactionListDto

    // ── Investments ─────────────────────────────────────
    @GET("investments/dashboard")
    suspend fun obterInvestmentDashboard(): InvestmentDashboardDto
    @GET("investments/")
    suspend fun listarInvestimentos(): InvestmentListDto
    @POST("investments/")
    suspend fun criarInvestimento(@Body dados: InvestmentCreateDto): InvestmentDto
    @POST("investments/{id}/redeem")
    suspend fun resgatarInvestimento(@Path("id") id: String): InvestmentDto

    // ── Stocks ──────────────────────────────────────────
    @GET("stocks/")
    suspend fun obterAcoes(): StockListDto
}

