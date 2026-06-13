package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.ui.components.TransactionCard

@Composable
fun TransactionList(
    state: HomeUiState,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {

    LazyColumn {
        items(state.transactions, key = { it.id }) { transaction ->
            TransactionCard(
                transaction,
                onEdit = { onEdit(transaction) },
                onDelete = { onDelete(transaction) },
                modifier = Modifier.animateItem())
        }
    }
}