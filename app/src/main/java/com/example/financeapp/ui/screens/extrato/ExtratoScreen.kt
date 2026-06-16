package com.example.financeapp.ui.screens.extrato

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.components.IFBankBackground
import com.example.financeapp.ui.theme.*
import com.example.financeapp.utils.formatCurrencyBr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── State ──────────────────────────────────────────────
data class ExtratoUiState(
    val carregando: Boolean = false,
    val transacoes: List<Transaction> = emptyList(),
    val filtro: String = "all",
    val exportando: Boolean = false,
    val mensagem: String? = null
)

// ── ViewModel ──────────────────────────────────────────
class ExtratoViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(ExtratoUiState())
    val estado: StateFlow<ExtratoUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val transacoes = transactionRepository.obterTransacoes().firstOrNull() ?: emptyList()
                _estado.update { it.copy(carregando = false, transacoes = transacoes) }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false, mensagem = "Erro ao carregar") }
            }
        }
    }

    fun filtrar(filtro: String) {
        _estado.update { it.copy(filtro = filtro) }
    }

    fun getTransacoesFiltradas(): List<Transaction> {
        val state = _estado.value
        return when (state.filtro) {
            "income" -> state.transacoes.filter { it.type == TransactionType.INCOME }
            "expense" -> state.transacoes.filter { it.type == TransactionType.EXPENSE }
            else -> state.transacoes
        }
    }

    fun gerarCsv(context: Context) {
        viewModelScope.launch {
            _estado.update { it.copy(exportando = true) }
            try {
                val transacoes = getTransacoesFiltradas()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val csv = buildString {
                    appendLine("Data,Descrição,Tipo,Valor")
                    transacoes.forEach { tx ->
                        val tipo = if (tx.type == TransactionType.INCOME) "Entrada" else "Saída"
                        appendLine("${tx.date.format(formatter)},\"${tx.description}\",$tipo,${tx.amount}")
                    }
                }

                val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "IF Bank")
                dir.mkdirs()
                val file = File(dir, "extrato_ifbank_${System.currentTimeMillis()}.csv")
                file.writeText(csv)

                // Share
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Extrato IF Bank")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartilhar extrato"))

                _estado.update { it.copy(exportando = false, mensagem = "Extrato exportado!") }
            } catch (e: Exception) {
                _estado.update { it.copy(exportando = false, mensagem = "Erro ao exportar: ${e.message}") }
            }
        }
    }

    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(
    aoVoltar: () -> Unit,
    viewModel: ExtratoViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val transacoesFiltradas = viewModel.getTransacoesFiltradas()

    val totalEntradas = transacoesFiltradas.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalSaidas = transacoesFiltradas.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Extrato", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${transacoesFiltradas.size} movimentações", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        IFBankBackground {
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Resumo card
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = IFIncome.copy(alpha = 0.1f)), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Entradas", style = MaterialTheme.typography.labelSmall, color = IFIncome)
                            Text(formatCurrencyBr(totalEntradas), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = IFIncome)
                        }
                    }
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = IFExpense.copy(alpha = 0.1f)), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Saídas", style = MaterialTheme.typography.labelSmall, color = IFExpense)
                            Text(formatCurrencyBr(totalSaidas), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = IFExpense)
                        }
                    }
                }

                // Filtros + Exportar
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("all" to "Todos", "income" to "Entradas", "expense" to "Saídas").forEach { (key, label) ->
                            FilterChip(selected = estado.filtro == key, onClick = { viewModel.filtrar(key) }, label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Botão exportar
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    if (estado.exportando) {
                        CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.gerarCsv(context) },
                            shape = MaterialTheme.shapes.medium,
                            enabled = transacoesFiltradas.isNotEmpty()
                        ) {
                            Icon(Icons.Default.FileDownload, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Exportar CSV")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (estado.carregando) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                } else if (transacoesFiltradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Receipt, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            Spacer(Modifier.height(16.dp))
                            Text("Nenhuma movimentação", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(transacoesFiltradas, key = { it.id }) { tx ->
                            val isIncome = tx.type == TransactionType.INCOME
                            val isDark = isSystemInDarkTheme()
                            val color = if (isIncome) { if (isDark) IFIncomeDark else IFIncome } else { if (isDark) IFExpenseDark else IFExpense }

                            Card(
                                Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                            null, tint = color, modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(tx.description, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(tx.date.format(dateFmt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        (if (isIncome) "+ " else "- ") + formatCurrencyBr(tx.amount),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold, color = color
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}
