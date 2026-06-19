package com.example.financeapp.ui.screens.deposito

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.DepositRequestDto
import com.example.financeapp.domain.repository.FinanceRepository
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.theme.*
import com.example.financeapp.utils.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ── State ──────────────────────────────────────────────
data class DepositoUiState(
    val tipo: String = "PIX", val valor: String = "", val descricao: String = "",
    val enviando: Boolean = false, val sucesso: Boolean = false, val mensagem: String? = null
)

// ── ViewModel ──────────────────────────────────────────
class DepositoViewModel(private val repo: FinanceRepository) : ViewModel() {
    private val _estado = MutableStateFlow(DepositoUiState())
    val estado: StateFlow<DepositoUiState> = _estado.asStateFlow()

    fun atualizarTipo(v: String) { _estado.update { it.copy(tipo = v) } }
    fun atualizarValor(v: String) { _estado.update { it.copy(valor = v) } }
    fun atualizarDescricao(v: String) { _estado.update { it.copy(descricao = v) } }

    fun depositar() {
        val valorDouble = (_estado.value.valor.toLongOrNull() ?: 0L) / 100.0
        if (valorDouble <= 0) { _estado.update { it.copy(mensagem = "Informe um valor válido") }; return }
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                repo.criarDeposito(DepositRequestDto(amount = valorDouble, depositType = _estado.value.tipo, description = _estado.value.descricao.ifBlank { null }))
                _estado.update { it.copy(enviando = false, sucesso = true, mensagem = "Depósito de R$ ${String.format("%.2f", valorDouble)} realizado!") }
            } catch (e: Exception) { _estado.update { it.copy(enviando = false, mensagem = "Erro ao depositar") } }
        }
    }

    fun novo() { _estado.update { DepositoUiState() } }
    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositoScreen(aoVoltar: () -> Unit, viewModel: DepositoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val tfColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline, focusedLabelColor = MaterialTheme.colorScheme.primary)

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("Depositar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }, navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            if (estado.sucesso) {
                Spacer(Modifier.height(48.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(72.dp).clip(CircleShape).background(IFIncome.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, "Sucesso", tint = IFIncome, modifier = Modifier.size(40.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Depósito realizado!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = IFIncome, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                GradientButton("Novo depósito", onClick = { viewModel.novo() })
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = aoVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar", color = MaterialTheme.colorScheme.primary) }
            } else {
                Spacer(Modifier.height(16.dp))
                Text("Como deseja depositar?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("PIX" to Icons.Default.Pix, "BOLETO" to Icons.Default.Receipt, "TED" to Icons.Default.AccountBalance).forEach { (tipo, icon) ->
                        Card(
                            modifier = Modifier.weight(1f).clickable { viewModel.atualizarTipo(tipo) },
                            colors = CardDefaults.cardColors(containerColor = if (estado.tipo == tipo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(icon, tipo, tint = if (estado.tipo == tipo) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(6.dp))
                                Text(tipo, style = MaterialTheme.typography.labelMedium, color = if (estado.tipo == tipo) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                val amountField = remember(estado.valor) { val f = formatCurrency(estado.valor); TextFieldValue(f, TextRange(f.length)) }
                OutlinedTextField(amountField, { viewModel.atualizarValor(it.text.replace("\\D".toRegex(), "")) }, label = { Text("Valor") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = tfColors, textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(estado.descricao, viewModel::atualizarDescricao, label = { Text("Descrição (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                Spacer(Modifier.height(32.dp))

                if (estado.enviando) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) } }
                else { GradientButton("Depositar", onClick = { viewModel.depositar() }, enabled = (estado.valor.toLongOrNull() ?: 0L) > 0) }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
