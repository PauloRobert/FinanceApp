package com.example.financeapp.domain.model

// Resultado das operações de autenticação
sealed class ResultadoAuth {
    data class Sucesso(val usuario: Usuario) : ResultadoAuth()
    data class Erro(val mensagem: String) : ResultadoAuth()
    data object Carregando : ResultadoAuth()
}
