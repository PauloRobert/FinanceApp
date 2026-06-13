package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {

    val state = viewModel.state
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    var showDeletDialog by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
        FloatingActionButton(onClick = { showBottomSheet = true }) {
            Text("+")
        }
    }) { padding ->
        if (showDeletDialog && selectedTransaction != null) {
            AlertDialog(
                onDismissRequest = { showDeletDialog = false },
                confirmButton = {
                    Button(onClick = {
                        val deleted = selectedTransaction!!

                        viewModel.deleteTransaction(deleted)
                        showDeletDialog = false

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Transação excluída",
                                actionLabel = "Desfazer",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addTransaction(
                                    deleted.description,
                                    deleted.amount,
                                    deleted.date,
                                    deleted.type == TransactionType.INCOME
                                )
                            }
                        }
                    }) { Text("Excluir") }
                },
                dismissButton = {
                    Button(onClick = {
                        showDeletDialog = false
                    }) { Text("Cancelar") }
                },
                title = {Text("Excluir transação")},
                text = {Text("Tem certeza que quer excluir a transação?")}

            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        )
        {
            SummarySection(state)
            Spacer(modifier = Modifier.height(16.dp))
            TransactionList(
                state,
                onEdit = { transaction ->
                    selectedTransaction = transaction
                    showBottomSheet = true
                },
                onDelete = { transaction ->
                    selectedTransaction = transaction
                    showDeletDialog = true
                }
            )
        }

        if (showBottomSheet) {
            TransactionBottomSheet(
                transaction = selectedTransaction,
                onDismiss = {
                    showBottomSheet = false
                    selectedTransaction = null
                },
                onSave = { description, value, date, isIncone ->
                    //lógica para inserir
                    if (selectedTransaction == null) {
                        //lógica para salvar
                        viewModel.addTransaction(description, value, date, isIncone)
                    } else {
                        //lógica para alterar
                        selectedTransaction?.let { transaction ->
                            viewModel.updateTransaction(
                                transaction.copy(
                                    description = description,
                                    amount = value,
                                    date = date,
                                    type = if (isIncone) TransactionType.INCOME else TransactionType.EXPENSE
                                )
                            )
                        }
                    }
                    showBottomSheet = false
                    selectedTransaction = null
                }
            )
        }
    }
}