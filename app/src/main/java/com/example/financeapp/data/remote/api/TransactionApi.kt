package com.example.financeapp.data.remote.api

import com.example.financeapp.data.remote.dto.LoginRequest
import com.example.financeapp.data.remote.dto.MessageResponse
import com.example.financeapp.data.remote.dto.PasswordChangeRequest
import com.example.financeapp.data.remote.dto.PrivacyResponse
import com.example.financeapp.data.remote.dto.PrivacyUpdateRequest
import com.example.financeapp.data.remote.dto.ProfileResponse
import com.example.financeapp.data.remote.dto.ProfileUpdateRequest
import com.example.financeapp.data.remote.dto.RegisterRequest
import com.example.financeapp.data.remote.dto.SecurityResponse
import com.example.financeapp.data.remote.dto.SecurityUpdateRequest
import com.example.financeapp.data.remote.dto.TokenResponse
import com.example.financeapp.data.remote.dto.TransactionListResponse
import com.example.financeapp.data.remote.dto.TransactionRemoteDto
import com.example.financeapp.data.remote.dto.TransactionRequest
import com.example.financeapp.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
}

