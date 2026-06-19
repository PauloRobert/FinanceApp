package com.example.financeapp.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.ui.components.BalanceCard
import com.example.financeapp.utils.formatCurrencyBr

@Composable
fun SummarySection(
    state: HomeUiState,
    saldoVisivel: Boolean,
    aoAlternarVisibilidade: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalEntradas = state.transacoes
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

    val totalSaidas = state.transacoes
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val saldo = totalEntradas - totalSaidas

    BalanceCard(
        saldo = saldo,
        totalEntradas = totalEntradas,
        totalSaidas = totalSaidas,
        saldoVisivel = saldoVisivel,
        aoAlternarVisibilidade = aoAlternarVisibilidade,
        formatarMoeda = { formatCurrencyBr(it) },
        modifier = modifier
    )
}