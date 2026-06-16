package com.example.financeapp.ui.screens.transferencia

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.BeneficiaryCreateRequest
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.domain.repository.TransferRepository
import com.example.financeapp.ui.components.GradientButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ── ViewModel ──────────────────────────────────────────

data class FavorecidosUiState(
    val carregando: Boolean = false,
    val lista: List<BeneficiaryDto> = emptyList(),
    val salvando: Boolean = false,
    val mensagem: String? = null
)

class FavorecidosViewModel(
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(FavorecidosUiState())
    val estado: StateFlow<FavorecidosUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val lista = transferRepository.listarFavorecidos()
                _estado.update { it.copy(carregando = false, lista = lista) }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false, mensagem = "Erro ao carregar") }
            }
        }
    }

    fun criar(dados: BeneficiaryCreateRequest) {
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            try {
                transferRepository.criarFavorecido(dados)
                _estado.update { it.copy(salvando = false, mensagem = "Favorecido salvo!") }
                carregar()
            } catch (e: Exception) {
                _estado.update { it.copy(salvando = false, mensagem = "Erro ao salvar") }
            }
        }
    }

    fun excluir(id: String) {
        viewModelScope.launch {
            try {
                transferRepository.excluirFavorecido(id)
                _estado.update { it.copy(mensagem = "Favorecido excluído") }
                carregar()
            } catch (e: Exception) {
                _estado.update { it.copy(mensagem = "Erro ao excluir") }
            }
        }
    }

    fun limparMensagem() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavorecidosScreen(
    aoVoltar: () -> Unit,
    viewModel: FavorecidosViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarCriar by remember { mutableStateOf(false) }
    var excluirId by remember { mutableStateOf<BeneficiaryDto?>(null) }

    LaunchedEffect(estado.mensagem) {
        estado.mensagem?.let { snackbarHostState.showSnackbar(it); viewModel.limparMensagem() }
    }

    excluirId?.let { fav ->
        AlertDialog(
            onDismissRequest = { excluirId = null },
            confirmButton = { TextButton(onClick = { viewModel.excluir(fav.id); excluirId = null }) { Text("Excluir", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } },
            title = { Text("Excluir favorecido", fontWeight = FontWeight.SemiBold) },
            text = { Text("Deseja excluir ${fav.name}?") },
            shape = MaterialTheme.shapes.large
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Favorecidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarCriar = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) {
                Icon(Icons.Default.Add, "Novo")
            }
        }
    ) { padding ->
        if (estado.carregando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        } else if (estado.lista.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhum favorecido", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Toque em + para cadastrar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Spacer(Modifier.height(8.dp)) }
                items(estado.lista, key = { it.id }) { fav ->
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(fav.nickname ?: fav.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(fav.bankName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Conta ****${fav.account.takeLast(4)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { excluirId = fav }) {
                                Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        if (mostrarCriar) {
            CriarFavorecidoSheet(estado.salvando, onDismiss = { mostrarCriar = false }) { dados ->
                viewModel.criar(dados); mostrarCriar = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CriarFavorecidoSheet(salvando: Boolean, onDismiss: () -> Unit, onSave: (BeneficiaryCreateRequest) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var banco by remember { mutableStateOf("") }
    var agencia by remember { mutableStateOf("") }
    var conta by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("CORRENTE") }
    var apelido by remember { mutableStateOf("") }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Novo favorecido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(nome, { nome = it }, label = { Text("Nome completo") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(cpf, { cpf = it }, label = { Text("CPF/CNPJ (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(banco, { banco = it }, label = { Text("Banco") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(agencia, { agencia = it }, label = { Text("Agência") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                OutlinedTextField(conta, { conta = it }, label = { Text("Conta") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("CORRENTE" to "Corrente", "POUPANCA" to "Poupança", "SALARIO" to "Salário").forEach { (k, v) ->
                    FilterChip(selected = tipo == k, onClick = { tipo = k }, label = { Text(v, style = MaterialTheme.typography.labelSmall) })
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(apelido, { apelido = it }, label = { Text("Apelido (opcional)") }, placeholder = { Text("Ex: Mãe, Contador") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
            Spacer(Modifier.height(20.dp))
            if (salvando) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp)) }
            } else {
                GradientButton("Salvar favorecido", onClick = {
                    if (nome.isNotBlank() && banco.isNotBlank() && conta.isNotBlank()) {
                        onSave(BeneficiaryCreateRequest(name = nome, cpfCnpj = cpf.ifBlank { null }, bankName = banco, agency = agencia.ifBlank { null }, account = conta, accountType = tipo, nickname = apelido.ifBlank { null }))
                    }
                }, enabled = nome.isNotBlank() && banco.isNotBlank() && conta.isNotBlank())
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
