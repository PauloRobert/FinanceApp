package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.utils.formatCurrencyBr

@Composable
fun SummarySection(state: HomeUiState) {
    //total de entradas
    val totalIncome = state.transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }
    //total de saídas
    val totalExpese = state.transactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    //saldo
    val balance = totalIncome - totalExpese

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E88E5)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp))
        {
            Text("Saldo", color = Color.White)
            Text(
                text = formatCurrencyBr(balance),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Entradas: ${formatCurrencyBr(totalIncome)}", color = Color.Green)

            Text("Saídas: ${formatCurrencyBr(totalExpese)}", color = Color(0xFFF3A1B3))

        }
    }
}