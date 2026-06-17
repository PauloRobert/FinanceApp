package com.example.financeapp.ui.screens.relatorios

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.TransactionRepository
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
import java.math.BigDecimal

// ── State ──────────────────────────────────────────────
data class RelatorioUiState(
    val carregando: Boolean = false,
    val totalEntradas: BigDecimal = BigDecimal.ZERO,
    val totalSaidas: BigDecimal = BigDecimal.ZERO,
    val saldo: BigDecimal = BigDecimal.ZERO,
    val qtdTransacoes: Int = 0,
    val maiorEntrada: Transaction? = null,
    val maiorSaida: Transaction? = null,
    val categorias: List<CategoriaResumo> = emptyList()
)

data class CategoriaResumo(val nome: String, val total: BigDecimal, val qtd: Int, val cor: Color)

// ── ViewModel ──────────────────────────────────────────
class RelatorioViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(RelatorioUiState())
    val estado: StateFlow<RelatorioUiState> = _estado.asStateFlow()

    init { carregar() }

    private fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val transacoes = transactionRepository.obterTransacoes().firstOrNull() ?: emptyList()
                val entradas = transacoes.filter { it.type == TransactionType.INCOME }
                val saidas = transacoes.filter { it.type == TransactionType.EXPENSE }
                val totalE = entradas.sumOf { it.amount }
                val totalS = saidas.sumOf { it.amount }

                // Agrupar por descrição como "categoria"
                val cores = listOf(IFBlue, IFPurple, IFLilac, IFIncome, IFWarning, Color(0xFF06B6D4), Color(0xFFF472B6))
                val grupos = saidas.groupBy { it.description.take(20) }
                    .map { (nome, txs) -> CategoriaResumo(nome, txs.sumOf { it.amount }, txs.size, cores[grupos@ 0]) }
                    .sortedByDescending { it.total }

                val categoriasComCor = grupos.mapIndexed { i, cat -> cat.copy(cor = cores[i % cores.size]) }

                _estado.update {
                    it.copy(
                        carregando = false,
                        totalEntradas = totalE, totalSaidas = totalS,
                        saldo = totalE - totalS, qtdTransacoes = transacoes.size,
                        maiorEntrada = entradas.maxByOrNull { it.amount },
                        maiorSaida = saidas.maxByOrNull { it.amount },
                        categorias = categoriasComCor
                    )
                }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false) }
            }
        }
    }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatorioScreen(
    aoVoltar: () -> Unit,
    viewModel: RelatorioViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Relatórios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        IFBankBackground {
            if (estado.carregando) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(Modifier.height(4.dp)) }

                    // Donut Chart
                    item { DonutChartCard(estado) }

                    // KPIs
                    item { KpiRow(estado) }

                    // Maior entrada/saída
                    item { DestaquesCard(estado) }

                    // Categorias
                    if (estado.categorias.isNotEmpty()) {
                        item {
                            Text("Gastos por categoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        estado.categorias.forEachIndexed { _, cat ->
                            item { CategoriaItem(cat, estado.totalSaidas) }
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DonutChartCard(estado: RelatorioUiState) {
    val isDark = isSystemInDarkTheme()
    val total = estado.totalEntradas + estado.totalSaidas
    val entradaPct = if (total > BigDecimal.ZERO) estado.totalEntradas.toFloat() / total.toFloat() else 0.5f

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Balanço Geral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))

            Box(Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val strokeW = 24f
                    val arcSize = Size(size.width - strokeW, size.height - strokeW)
                    val topLeft = Offset(strokeW / 2, strokeW / 2)

                    // Entradas (verde)
                    drawArc(
                        color = if (isDark) IFIncomeDark else IFIncome,
                        startAngle = -90f,
                        sweepAngle = 360f * entradaPct,
                        useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                    // Saídas (vermelho)
                    drawArc(
                        color = if (isDark) IFExpenseDark else IFExpense,
                        startAngle = -90f + 360f * entradaPct,
                        sweepAngle = 360f * (1 - entradaPct),
                        useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Saldo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatCurrencyBr(estado.saldo),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (estado.saldo >= BigDecimal.ZERO) IFIncome else IFExpense
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                LegendItem("Entradas", IFIncome, formatCurrencyBr(estado.totalEntradas))
                LegendItem("Saídas", IFExpense, formatCurrencyBr(estado.totalSaidas))
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, cor: Color, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(cor))
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun KpiRow(estado: RelatorioUiState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        KpiCard("Transações", "${estado.qtdTransacoes}", Icons.Default.Receipt, Modifier.weight(1f))
        KpiCard(
            "Ticket médio",
            if (estado.qtdTransacoes > 0) formatCurrencyBr((estado.totalEntradas + estado.totalSaidas) / BigDecimal(estado.qtdTransacoes)) else "R$ 0",
            Icons.Default.Analytics,
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCard(label: String, valor: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DestaquesCard(estado: RelatorioUiState) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Destaques", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            estado.maiorEntrada?.let { tx ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(IFIncome.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowUpward, null, tint = IFIncome, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Maior entrada", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tx.description, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Text("+ ${formatCurrencyBr(tx.amount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = IFIncome)
                }
            }

            if (estado.maiorEntrada != null && estado.maiorSaida != null) {
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }

            estado.maiorSaida?.let { tx ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(IFExpense.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowDownward, null, tint = IFExpense, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Maior saída", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tx.description, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Text("- ${formatCurrencyBr(tx.amount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = IFExpense)
                }
            }
        }
    }
}

@Composable
private fun CategoriaItem(cat: CategoriaResumo, totalSaidas: BigDecimal) {
    val pct = if (totalSaidas > BigDecimal.ZERO) (cat.total.toFloat() / totalSaidas.toFloat()) else 0f

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(cat.cor))
                    Spacer(Modifier.width(10.dp))
                    Text(cat.nome, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                Text(formatCurrencyBr(cat.total), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = IFExpense)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(MaterialTheme.shapes.small),
                color = cat.cor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text("${cat.qtd} transações • ${(pct * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
