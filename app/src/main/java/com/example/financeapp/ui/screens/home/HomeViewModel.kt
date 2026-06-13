package com.example.financeapp.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.SerialDescriptor
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

class HomeViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val insertTransactionsUseCase: InsertTransactionsUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {
    var state by mutableStateOf(HomeUiState())
        private set

    init {
        viewModelScope.launch {
            getTransactionsUseCase().collect { data ->
                state = state.copy(transactions = data)
            }
        }
    }


    fun addTransaction(
        description: String,
        amount: BigDecimal,
        date: LocalDateTime,
        type: Boolean
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount,
                date = date,
                type = if (type) TransactionType.INCOME else TransactionType.EXPENSE
            )
            insertTransactionsUseCase(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            updateTransactionUseCase(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction.id)
        }
    }
}