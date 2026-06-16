package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.ui.components.EmptyStateView
import com.example.financeapp.ui.components.IFBankHeader
import com.example.financeapp.ui.components.InsightsRow
import com.example.financeapp.ui.components.QuickActionsRow
import com.example.financeapp.utils.formatCurrencyBr
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    aoAbrirConfiguracoes: () -> Unit = {},
    aoAbrirPix: () -> Unit = {},
    aoAbrirTransferencia: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    var transacaoSelecionada by remember { mutableStateOf<Transaction?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()
    var mostrarDialogoExclusao by remember { mutableStateOf(false) }
    var saldoVisivel by rememberSaveable { mutableStateOf(true) }

    // Cálculos para insights
    val totalEntradas = estado.transacoes
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }
    val totalSaidas = estado.transacoes
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            viewModel.limparErro()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transacaoSelecionada = null
                    mostrarBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Nova transação",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        // Diálogo de exclusão
        if (mostrarDialogoExclusao && transacaoSelecionada != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoExclusao = false },
                confirmButton = {
                    Button(
                        onClick = {
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
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Excluir") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoExclusao = false }) {
                        Text("Cancelar")
                    }
                },
                title = {
                    Text(
                        "Excluir transação",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = { Text("Tem certeza que deseja excluir esta transação?") },
                shape = MaterialTheme.shapes.large
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header premium com saudação
            IFBankHeader(
                nomeUsuario = estado.nomeUsuario,
                aoAbrirConfiguracoes = aoAbrirConfiguracoes
            )

            if (estado.carregando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    // Balance Card com gradiente
                    SummarySection(
                        state = estado,
                        saldoVisivel = saldoVisivel,
                        aoAlternarVisibilidade = { saldoVisivel = !saldoVisivel }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Ações rápidas
                    QuickActionsRow(aoAbrirPix = aoAbrirPix, aoAbrirTransferencia = aoAbrirTransferencia)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Insights financeiros
                    if (estado.transacoes.isNotEmpty()) {
                        InsightsRow(
                            totalEntradas = totalEntradas,
                            totalSaidas = totalSaidas,
                            formatarMoeda = { formatCurrencyBr(it) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Header da seção de transações
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Últimas movimentações",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${estado.transacoes.size} itens",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista ou Empty State
                    if (estado.transacoes.isEmpty()) {
                        EmptyStateView()
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