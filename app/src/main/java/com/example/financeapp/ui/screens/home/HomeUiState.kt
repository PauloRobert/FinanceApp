package com.example.financeapp.ui.screens.home

import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.model.Transaction

data class HomeUiState(
    val transacoes: List<Transaction> = emptyList(),
    val origemAtual: OrigemDados = OrigemDados.FIREBASE,
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val nomeUsuario: String = ""
)