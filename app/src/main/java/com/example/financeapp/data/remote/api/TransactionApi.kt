package com.example.financeapp.data.remote.api

import com.example.financeapp.data.remote.dto.LoginRequest
import com.example.financeapp.data.remote.dto.TokenResponse
import com.example.financeapp.data.remote.dto.TransactionListResponse
import com.example.financeapp.data.remote.dto.TransactionRemoteDto
import com.example.financeapp.data.remote.dto.TransactionRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionApi {

    @POST("auth/login")
    suspend fun login(@Body credenciais: LoginRequest): TokenResponse

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
}

