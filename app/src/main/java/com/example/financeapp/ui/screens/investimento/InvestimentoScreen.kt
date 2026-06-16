package com.example.financeapp.ui.screens.investimento

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.InvestmentCreateDto
import com.example.financeapp.data.remote.dto.InvestmentDto
import com.example.financeapp.domain.repository.FinanceRepository
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

// ── State ──────────────────────────────────────────────
data class InvestimentoUiState(
    val carregando: Boolean = false, val salvando: Boolean = false,
    val totalInvestido: Double = 0.0, val totalAtual: Double = 0.0,
    val lucro: Double = 0.0, val lucroPct: Double = 0.0,
    val investimentos: List<InvestmentDto> = emptyList(),
    val mensagem: String? = null
)

// ── ViewModel ──────────────────────────────────────────
class InvestimentoViewModel(private val repo: FinanceRepository) : ViewModel() {
    private val _estado = MutableStateFlow(InvestimentoUiState())
    val estado: StateFlow<InvestimentoUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val dash = repo.obterInvestmentDashboard()
                val list = repo.listarInvestimentos()
                _estado.update { it.copy(carregando = false, totalInvestido = dash.totalInvested, totalAtual = dash.totalCurrentValue, lucro = dash.totalProfit, lucroPct = dash.profitPercentage, investimentos = list.investments) }
            } catch (e: Exception) { _estado.update { it.copy(carregando = false, mensagem = "Erro ao carregar") } }
        }
    }

    fun investir(nome: String, tipo: String, valor: Double) {
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            try {
                repo.criarInvestimento(InvestmentCreateDto(name = nome, investmentType = tipo, amount = valor))
                _estado.update { it.copy(salvando = false, mensagem = "Investimento realizado!") }
                carregar()
            } catch (e: retrofit2.HttpException) {
                _estado.update { it.copy(salvando = false, mensagem = if (e.code() == 400) "Saldo insuficiente" else "Erro") }
            } catch (e: Exception) { _estado.update { it.copy(salvando = false, mensagem = "Erro de conexão") } }
        }
    }

    fun resgatar(id: String) {
        viewModelScope.launch {
            try {
                repo.resgatarInvestimento(id)
                _estado.update { it.copy(mensagem = "Resgate realizado!") }
                carregar()
            } catch (e: Exception) { _estado.update { it.copy(mensagem = "Erro ao resgatar") } }
        }
    }

    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestimentoScreen(aoVoltar: () -> Unit, viewModel: InvestimentoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var mostrarInvestir by remember { mutableStateOf(false) }
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }, containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("Investimentos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        floatingActionButton = { FloatingActionButton(onClick = { mostrarInvestir = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) { Icon(Icons.Default.Add, "Investir") } }
    ) { padding ->
        if (estado.carregando) { Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } }
        else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Spacer(Modifier.height(8.dp)) }
                // Dashboard card
                item { InvestmentDashboardCard(estado, fmt) }
                // Investimentos ativos
                item {
                    if (estado.investimentos.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Seus investimentos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
                items(estado.investimentos.filter { it.status == "ACTIVE" }, key = { it.id }) { inv ->
                    InvestimentoItem(inv, fmt) { viewModel.resgatar(inv.id) }
                }
                if (estado.investimentos.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Savings, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                Spacer(Modifier.height(16.dp))
                                Text("Comece a investir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Toque em + para aplicar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        if (mostrarInvestir) { InvestirSheet(estado.salvando, onDismiss = { mostrarInvestir = false }) { n, t, v -> viewModel.investir(n, t, v); mostrarInvestir = false } }
    }
}

@Composable
private fun InvestmentDashboardCard(estado: InvestimentoUiState, fmt: NumberFormat) {
    val isDark = isSystemInDarkTheme()
    val gradient = if (isDark) IFGradientDarkCard else IFGradientCard

    Column(Modifier.fillMaxWidth().shadow(12.dp, MaterialTheme.shapes.large).clip(MaterialTheme.shapes.large).background(gradient).padding(24.dp)) {
        Text("Patrimônio investido", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        Spacer(Modifier.height(4.dp))
        Text(fmt.format(estado.totalAtual), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("Investido", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f)); Text(fmt.format(estado.totalInvestido), style = MaterialTheme.typography.titleSmall, color = Color.White) }
            Column(horizontalAlignment = Alignment.End) {
                Text("Rendimento", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                Text("${fmt.format(estado.lucro)} (${String.format("%.1f", estado.lucroPct)}%)",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                    color = if (estado.lucro >= 0) IFIncome else Color(0xFFFCA5A5))
            }
        }
    }
}

@Composable
private fun InvestimentoItem(inv: InvestmentDto, fmt: NumberFormat, onResgatar: () -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Savings, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(inv.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("${inv.investmentType} • ${inv.annualRate}% a.a.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(fmt.format(inv.currentValue), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    val profit = inv.currentValue - inv.amountInvested
                    Text((if (profit >= 0) "+" else "") + fmt.format(profit), style = MaterialTheme.typography.bodySmall, color = if (profit >= 0) IFIncome else IFExpense)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onResgatar, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
                Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Resgatar", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvestirSheet(salvando: Boolean, onDismiss: () -> Unit, onSave: (String, String, Double) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tipo by remember { mutableStateOf("CDB") }
    var valor by remember { mutableStateOf("") }
    val tfColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
    val nomes = mapOf("CDB" to "CDB 120% CDI", "LCI" to "LCI Isenta", "LCA" to "LCA Agro", "TESOURO" to "Tesouro Selic", "FUNDO" to "Fundo Multi", "ACAO" to "Ações BR")
    val taxas = mapOf("CDB" to "12.5%", "LCI" to "10.8%", "LCA" to "10.5%", "TESOURO" to "11.2%", "FUNDO" to "15.0%", "ACAO" to "18.0%")

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Investir", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            Text("Tipo", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            // Chips em 2 linhas
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("CDB", "LCI", "LCA").forEach { t ->
                    FilterChip(selected = tipo == t, onClick = { tipo = t }, label = { Text(t, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("TESOURO", "FUNDO", "ACAO").forEach { t ->
                    FilterChip(selected = tipo == t, onClick = { tipo = t }, label = { Text(t, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("${nomes[tipo]} — ${taxas[tipo]} a.a.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(valor, { valor = it }, label = { Text("Valor (R$)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            Spacer(Modifier.height(24.dp))
            if (salvando) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) } }
            else { GradientButton("Investir", onClick = { val v = valor.replace(",", ".").toDoubleOrNull(); if (v != null && v > 0) onSave(nomes[tipo] ?: tipo, tipo, v) }, enabled = (valor.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0) }
            Spacer(Modifier.height(20.dp))
        }
    }
}
