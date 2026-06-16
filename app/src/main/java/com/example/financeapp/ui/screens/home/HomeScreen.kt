package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    aoAbrirConfiguracoes: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    var transacaoSelecionada by remember { mutableStateOf<Transaction?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()
    var mostrarDialogoExclusao by remember { mutableStateOf(false) }

    // Exibir erros via Snackbar
    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            viewModel.limparErro()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Finance App")
                        if (estado.nomeUsuario.isNotBlank()) {
                            Text(
                                text = "Olá, ${estado.nomeUsuario}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = aoAbrirConfiguracoes) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarBottomSheet = true }) {
                Text("+")
            }
        }
    ) { padding ->
        if (mostrarDialogoExclusao && transacaoSelecionada != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoExclusao = false },
                confirmButton = {
                    Button(onClick = {
                        val excluida = transacaoSelecionada!!
                        viewModel.deletarTransacao(excluida)
                        mostrarDialogoExclusao = false

                        escopo.launch {
                            val resultado = snackbarHostState.showSnackbar(
                                message = "Transação excluída",
                                actionLabel = "Desfazer",
                                duration = SnackbarDuration.Short
                            )
                            if (resultado == SnackbarResult.ActionPerformed) {
                                viewModel.adicionarTransacao(
                                    excluida.description,
                                    excluida.amount,
                                    excluida.date,
                                    excluida.type == TransactionType.INCOME
                                )
                            }
                        }
                    }) { Text("Excluir") }
                },
                dismissButton = {
                    Button(onClick = { mostrarDialogoExclusao = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Excluir transação") },
                text = { Text("Tem certeza que quer excluir a transação?") }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            SummarySection(estado)
            Spacer(modifier = Modifier.height(12.dp))

            if (estado.carregando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (estado.transacoes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma transação encontrada",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                TransactionList(
                    estado,
                    onEdit = { transacao ->
                        transacaoSelecionada = transacao
                        mostrarBottomSheet = true
                    },
                    onDelete = { transacao ->
                        transacaoSelecionada = transacao
                        mostrarDialogoExclusao = true
                    }
                )
            }
        }

        if (mostrarBottomSheet) {
            TransactionBottomSheet(
                transaction = transacaoSelecionada,
                onDismiss = {
                    mostrarBottomSheet = false
                    transacaoSelecionada = null
                },
                onSave = { descricao, valor, data, ehEntrada ->
                    if (transacaoSelecionada == null) {
                        viewModel.adicionarTransacao(descricao, valor, data, ehEntrada)
                    } else {
                        transacaoSelecionada?.let { transacao ->
                            viewModel.atualizarTransacao(
                                transacao.copy(
                                    description = descricao,
                                    amount = valor,
                                    date = data,
                                    type = if (ehEntrada) TransactionType.INCOME else TransactionType.EXPENSE
                                )
                            )
                        }
                    }
                    mostrarBottomSheet = false
                    transacaoSelecionada = null
                }
            )
        }
    }
}