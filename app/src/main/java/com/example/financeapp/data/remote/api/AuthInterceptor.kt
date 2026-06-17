package com.example.financeapp.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

// Interceptor que adiciona o token JWT em todas as requisições
class AuthInterceptor(@Volatile private var token: String = "") : Interceptor {

    fun atualizarToken(novoToken: String) {
        token = novoToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requisicao = chain.request().newBuilder()
        if (token.isNotBlank()) {
            requisicao.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requisicao.build())
    }
}

