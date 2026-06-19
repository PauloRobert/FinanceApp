package com.example.financeapp.ui.screens.pagamento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.PaymentRequestDto
import com.example.financeapp.domain.repository.FinanceRepository
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.utils.formatCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ── State ──────────────────────────────────────────────
data class PagamentoUiState(
    val etapa: PagamentoEtapa = PagamentoEtapa.DADOS,
    val tipo: String = "BOLETO", val codigoBarras: String = "",
    val beneficiario: String = "", val valor: String = "",
    val descricao: String = "", val vencimento: String = "",
    val enviando: Boolean = false, val sucesso: Boolean = false, val mensagem: String? = null
)

enum class PagamentoEtapa { DADOS, CONFIRMACAO, COMPROVANTE }

// ── ViewModel ──────────────────────────────────────────
class PagamentoViewModel(private val repo: FinanceRepository) : ViewModel() {
    private val _estado = MutableStateFlow(PagamentoUiState())
    val estado: StateFlow<PagamentoUiState> = _estado.asStateFlow()

    fun atualizarTipo(v: String) { _estado.update { it.copy(tipo = v) } }
    fun atualizarCodigo(v: String) { _estado.update { it.copy(codigoBarras = v) } }
    fun atualizarBeneficiario(v: String) { _estado.update { it.copy(beneficiario = v) } }
    fun atualizarValor(v: String) { _estado.update { it.copy(valor = v) } }
    fun atualizarDescricao(v: String) { _estado.update { it.copy(descricao = v) } }

    fun avancar() {
        val s = _estado.value
        if (s.beneficiario.isBlank()) { _estado.update { it.copy(mensagem = "Informe o beneficiário") }; return }
        if ((s.valor.toLongOrNull() ?: 0L) <= 0) { _estado.update { it.copy(mensagem = "Informe o valor") }; return }
        _estado.update { it.copy(etapa = PagamentoEtapa.CONFIRMACAO) }
    }

    fun confirmar() {
        val s = _estado.value
        val valorDouble = (s.valor.toLongOrNull() ?: 0L) / 100.0
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            try {
                repo.criarPagamento(PaymentRequestDto(
                    barcode = s.codigoBarras.ifBlank { null }, payeeName = s.beneficiario,
                    amount = valorDouble, dueDate = s.vencimento.ifBlank { null },
                    paymentType = s.tipo, description = s.descricao.ifBlank { null }
                ))
                _estado.update { it.copy(enviando = false, sucesso = true, etapa = PagamentoEtapa.COMPROVANTE, mensagem = "Pagamento realizado!") }
            } catch (e: retrofit2.HttpException) {
                _estado.update { it.copy(enviando = false, mensagem = if (e.code() == 400) "Saldo insuficiente" else "Erro ao pagar") }
            } catch (e: Exception) { _estado.update { it.copy(enviando = false, mensagem = "Erro de conexão") } }
        }
    }

    fun voltar() { _estado.update { if (it.etapa == PagamentoEtapa.CONFIRMACAO) it.copy(etapa = PagamentoEtapa.DADOS) else it } }
    fun novo() { _estado.update { PagamentoUiState() } }
    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagamentoScreen(aoVoltar: () -> Unit, viewModel: PagamentoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val tfColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline, focusedLabelColor = MaterialTheme.colorScheme.primary)

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    val titulo = when (estado.etapa) { PagamentoEtapa.DADOS -> "Pagar"; PagamentoEtapa.CONFIRMACAO -> "Confirmar"; PagamentoEtapa.COMPROVANTE -> "Comprovante" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }, containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }, navigationIcon = {
            if (estado.etapa != PagamentoEtapa.COMPROVANTE) { IconButton(onClick = { if (estado.etapa == PagamentoEtapa.DADOS) aoVoltar() else viewModel.voltar() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            when (estado.etapa) {
                PagamentoEtapa.DADOS -> {
                    Spacer(Modifier.height(16.dp))
                    // Tipo
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("BOLETO" to "Boleto", "UTILITY" to "Conta", "TAX" to "Imposto").forEach { (k, v) ->
                            FilterChip(selected = estado.tipo == k, onClick = { viewModel.atualizarTipo(k) }, label = { Text(v) })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(estado.codigoBarras, viewModel::atualizarCodigo, label = { Text("Código de barras (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(estado.beneficiario, viewModel::atualizarBeneficiario, label = { Text("Beneficiário") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                    Spacer(Modifier.height(12.dp))
                    val amt = remember(estado.valor) { val f = formatCurrency(estado.valor); TextFieldValue(f, TextRange(f.length)) }
                    OutlinedTextField(amt, { viewModel.atualizarValor(it.text.replace("\\D".toRegex(), "")) }, label = { Text("Valor") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = tfColors, textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(estado.descricao, viewModel::atualizarDescricao, label = { Text("Descrição (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                    Spacer(Modifier.height(32.dp))
                    GradientButton("Continuar", onClick = { viewModel.avancar() })
                }
                PagamentoEtapa.CONFIRMACAO -> {
                    val valorDouble = (estado.valor.toLongOrNull() ?: 0L) / 100.0
                    Spacer(Modifier.height(16.dp))
                    Text("Confirme o pagamento", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(20.dp))
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(1.dp)) {
                        Column(Modifier.padding(20.dp)) {
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("Beneficiário", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(estado.beneficiario, fontWeight = FontWeight.Medium) }
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("Tipo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(estado.tipo, fontWeight = FontWeight.Medium) }
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("Valor", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("R$ ${String.format("%.2f", valorDouble)}", fontWeight = FontWeight.Medium) }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    if (estado.enviando) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) } }
                    else { GradientButton("Confirmar pagamento", onClick = { viewModel.confirmar() }); Spacer(Modifier.height(12.dp)); TextButton(onClick = { viewModel.voltar() }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = MaterialTheme.colorScheme.error) } }
                }
                PagamentoEtapa.COMPROVANTE -> {
                    Spacer(Modifier.height(48.dp))
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Box(Modifier.size(72.dp).clip(CircleShape).background(IFIncome.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.CheckCircle, "Sucesso", tint = IFIncome, modifier = Modifier.size(40.dp)) } }
                    Spacer(Modifier.height(16.dp))
                    Text("Pagamento realizado!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = IFIncome, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(32.dp))
                    GradientButton("Novo pagamento", onClick = { viewModel.novo() })
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = aoVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao início", color = MaterialTheme.colorScheme.primary) }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
