package com.example.financeapp.ui.screens.cartoes

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.CardCreateDto
import com.example.financeapp.data.remote.dto.CardDto
import com.example.financeapp.data.remote.dto.CardUpdateDto
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
data class CartoesUiState(
    val carregando: Boolean = false, val cartoes: List<CardDto> = emptyList(),
    val salvando: Boolean = false, val mensagem: String? = null
)

// ── ViewModel ──────────────────────────────────────────
class CartoesViewModel(private val repo: FinanceRepository) : ViewModel() {
    private val _estado = MutableStateFlow(CartoesUiState())
    val estado: StateFlow<CartoesUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val cartoes = repo.listarCartoes()
                _estado.update { it.copy(carregando = false, cartoes = cartoes) }
            } catch (e: Exception) { _estado.update { it.copy(carregando = false, mensagem = "Erro ao carregar") } }
        }
    }

    fun criarCartao(nome: String, tipo: String, limite: Double) {
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            try {
                repo.criarCartao(CardCreateDto(cardName = nome, cardType = tipo, creditLimit = limite))
                _estado.update { it.copy(salvando = false, mensagem = "Cartão criado!") }
                carregar()
            } catch (e: Exception) { _estado.update { it.copy(salvando = false, mensagem = "Erro ao criar") } }
        }
    }

    fun bloquear(id: String, bloquear: Boolean) {
        viewModelScope.launch {
            try {
                repo.atualizarCartao(id, CardUpdateDto(status = if (bloquear) "BLOCKED" else "ACTIVE"))
                _estado.update { it.copy(mensagem = if (bloquear) "Cartão bloqueado" else "Cartão desbloqueado") }
                carregar()
            } catch (e: Exception) { _estado.update { it.copy(mensagem = "Erro ao atualizar") } }
        }
    }

    fun toggleContactless(id: String, value: Boolean) {
        viewModelScope.launch {
            try { repo.atualizarCartao(id, CardUpdateDto(isContactless = value)); carregar() } catch (_: Exception) {}
        }
    }

    fun toggleInternacional(id: String, value: Boolean) {
        viewModelScope.launch {
            try { repo.atualizarCartao(id, CardUpdateDto(isInternational = value)); carregar() } catch (_: Exception) {}
        }
    }

    fun cancelar(id: String) {
        viewModelScope.launch {
            try { repo.cancelarCartao(id); _estado.update { it.copy(mensagem = "Cartão cancelado") }; carregar() }
            catch (e: Exception) { _estado.update { it.copy(mensagem = "Erro ao cancelar") } }
        }
    }

    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartoesScreen(aoVoltar: () -> Unit, viewModel: CartoesViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var mostrarCriar by remember { mutableStateOf(false) }
    var cartaoDetalhe by remember { mutableStateOf<CardDto?>(null) }
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }, containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("Cartões", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) },
        floatingActionButton = { FloatingActionButton(onClick = { mostrarCriar = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) { Icon(Icons.Default.Add, "Novo") } }
    ) { padding ->
        if (estado.carregando) { Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } }
        else if (estado.cartoes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CreditCard, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhum cartão", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Toque em + para solicitar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Spacer(Modifier.height(8.dp)) }
                items(estado.cartoes, key = { it.id }) { card ->
                    CartaoVisual(card, fmt, onBloquear = { viewModel.bloquear(card.id, card.status == "ACTIVE") },
                        onContactless = { viewModel.toggleContactless(card.id, it) },
                        onInternacional = { viewModel.toggleInternacional(card.id, it) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        // Bottom Sheet criar cartão
        if (mostrarCriar) { CriarCartaoSheet(estado.salvando, onDismiss = { mostrarCriar = false }) { n, t, l -> viewModel.criarCartao(n, t, l); mostrarCriar = false } }
    }
}

@Composable
private fun CartaoVisual(card: CardDto, fmt: NumberFormat, onBloquear: () -> Unit, onContactless: (Boolean) -> Unit, onInternacional: (Boolean) -> Unit) {
    val isDark = isSystemInDarkTheme()
    val gradient = if (card.cardType == "CREDIT") IFGradientAccent else if (isDark) IFGradientDarkCard else IFGradientCard
    val isBlocked = card.status == "BLOCKED"

    Column(
        modifier = Modifier.fillMaxWidth().shadow(12.dp, MaterialTheme.shapes.large).clip(MaterialTheme.shapes.large).background(gradient).padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("IF Bank", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(card.cardType, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
        }
        Spacer(Modifier.height(24.dp))
        Text("•••• •••• •••• ${card.lastFour}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = androidx.compose.ui.unit.TextUnit(3f, androidx.compose.ui.unit.TextUnitType.Sp))
        Spacer(Modifier.height(8.dp))
        Text(card.cardName, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        if (card.cardType == "CREDIT") {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Limite", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f)); Text(fmt.format(card.creditLimit), style = MaterialTheme.typography.titleSmall, color = Color.White) }
                Column(horizontalAlignment = Alignment.End) { Text("Disponível", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f)); Text(fmt.format(card.availableLimit), style = MaterialTheme.typography.titleSmall, color = IFIncome) }
            }
        }
        if (isBlocked) { Spacer(Modifier.height(8.dp)); Text("BLOQUEADO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5)) }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBloquear, shape = MaterialTheme.shapes.small, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White), modifier = Modifier.weight(1f)) {
                Icon(if (isBlocked) Icons.Default.LockOpen else Icons.Default.Lock, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isBlocked) "Desbloquear" else "Bloquear", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Contactless", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
            Switch(checked = card.isContactless, onCheckedChange = onContactless, colors = SwitchDefaults.colors(checkedTrackColor = Color.White.copy(alpha = 0.3f), checkedThumbColor = Color.White))
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Internacional", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
            Switch(checked = card.isInternational, onCheckedChange = onInternacional, colors = SwitchDefaults.colors(checkedTrackColor = Color.White.copy(alpha = 0.3f), checkedThumbColor = Color.White))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CriarCartaoSheet(salvando: Boolean, onDismiss: () -> Unit, onSave: (String, String, Double) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("DEBIT") }
    var limite by remember { mutableStateOf("") }
    val tfColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Solicitar cartão", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(nome, { nome = it }, label = { Text("Nome do cartão") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("DEBIT" to "Débito", "CREDIT" to "Crédito", "VIRTUAL" to "Virtual").forEach { (k, v) ->
                    FilterChip(selected = tipo == k, onClick = { tipo = k }, label = { Text(v, style = MaterialTheme.typography.labelSmall) })
                }
            }
            if (tipo == "CREDIT") {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(limite, { limite = it }, label = { Text("Limite de crédito") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Spacer(Modifier.height(24.dp))
            if (salvando) { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) } }
            else { GradientButton("Solicitar", onClick = { if (nome.isNotBlank()) onSave(nome, tipo, limite.toDoubleOrNull() ?: 0.0) }, enabled = nome.isNotBlank()) }
            Spacer(Modifier.height(20.dp))
        }
    }
}
