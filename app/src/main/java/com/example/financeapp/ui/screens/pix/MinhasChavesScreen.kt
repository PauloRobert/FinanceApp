package com.example.financeapp.ui.screens.pix

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
import androidx.compose.material.icons.filled.Key
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
import com.example.financeapp.data.remote.dto.PixKeyCreateRequest
import com.example.financeapp.data.remote.dto.PixKeyDto
import com.example.financeapp.domain.repository.PixRepository
import com.example.financeapp.ui.components.GradientButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ── ViewModel ──────────────────────────────────────────

data class MinhasChavesUiState(
    val carregando: Boolean = false,
    val chaves: List<PixKeyDto> = emptyList(),
    val salvando: Boolean = false,
    val mensagem: String? = null
)

class MinhasChavesViewModel(
    private val pixRepository: PixRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(MinhasChavesUiState())
    val estado: StateFlow<MinhasChavesUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val chaves = pixRepository.listarChaves()
                _estado.update { it.copy(carregando = false, chaves = chaves) }
            } catch (e: Exception) {
                _estado.update { it.copy(carregando = false, mensagem = "Erro ao carregar") }
            }
        }
    }

    fun criarChave(tipo: String, valor: String) {
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            try {
                pixRepository.criarChave(PixKeyCreateRequest(keyType = tipo, keyValue = valor))
                _estado.update { it.copy(salvando = false, mensagem = "Chave criada com sucesso!") }
                carregar()
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    400 -> "Limite de chaves atingido"
                    409 -> "Esta chave já está cadastrada"
                    else -> "Erro ao criar chave"
                }
                _estado.update { it.copy(salvando = false, mensagem = msg) }
            } catch (e: Exception) {
                _estado.update { it.copy(salvando = false, mensagem = "Erro de conexão") }
            }
        }
    }

    fun excluirChave(keyId: String) {
        viewModelScope.launch {
            try {
                pixRepository.excluirChave(keyId)
                _estado.update { it.copy(mensagem = "Chave excluída") }
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
fun MinhasChavesScreen(
    aoVoltar: () -> Unit,
    viewModel: MinhasChavesViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarCriar by remember { mutableStateOf(false) }
    var chaveExcluir by remember { mutableStateOf<PixKeyDto?>(null) }

    LaunchedEffect(estado.mensagem) {
        estado.mensagem?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagem()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Minhas Chaves", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarCriar = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Criar chave")
            }
        }
    ) { padding ->
        // Diálogo de exclusão
        chaveExcluir?.let { chave ->
            AlertDialog(
                onDismissRequest = { chaveExcluir = null },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.excluirChave(chave.id)
                        chaveExcluir = null
                    }) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { chaveExcluir = null }) { Text("Cancelar") }
                },
                title = { Text("Excluir chave", fontWeight = FontWeight.SemiBold) },
                text = { Text("Deseja excluir a chave ${chave.keyValue}?") },
                shape = MaterialTheme.shapes.large
            )
        }

        if (estado.carregando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (estado.chaves.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Key, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhuma chave cadastrada", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Toque em + para criar sua primeira chave", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                item {
                    Text("${estado.chaves.size}/5 chaves cadastradas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                }
                items(estado.chaves, key = { it.id }) { chave ->
                    ChaveItem(chave = chave, onDelete = { chaveExcluir = chave })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        if (mostrarCriar) {
            CriarChaveBottomSheet(
                salvando = estado.salvando,
                onDismiss = { mostrarCriar = false },
                onSave = { tipo, valor ->
                    viewModel.criarChave(tipo, valor)
                    mostrarCriar = false
                }
            )
        }
    }
}

@Composable
private fun ChaveItem(chave: PixKeyDto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Key, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(chave.keyType, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Text(chave.keyValue, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CriarChaveBottomSheet(
    salvando: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tipoSelecionado by remember { mutableStateOf("CPF") }
    var valorChave by remember { mutableStateOf("") }
    val tipos = listOf("CPF", "CNPJ", "EMAIL", "PHONE", "RANDOM")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Text("Nova chave Pix", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))

            Text("Tipo da chave", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipos.take(3).forEach { tipo ->
                    FilterChip(
                        selected = tipoSelecionado == tipo,
                        onClick = { tipoSelecionado = tipo },
                        label = { Text(tipo, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tipos.drop(3).forEach { tipo ->
                    FilterChip(
                        selected = tipoSelecionado == tipo,
                        onClick = { tipoSelecionado = tipo },
                        label = { Text(if (tipo == "PHONE") "Telefone" else if (tipo == "RANDOM") "Aleatória" else tipo, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (tipoSelecionado != "RANDOM") {
                val placeholder = when (tipoSelecionado) {
                    "CPF" -> "000.000.000-00"
                    "CNPJ" -> "00.000.000/0001-00"
                    "EMAIL" -> "seu@email.com"
                    "PHONE" -> "+55 11 99999-0000"
                    else -> ""
                }

                OutlinedTextField(
                    value = valorChave,
                    onValueChange = { valorChave = it },
                    label = { Text("Valor da chave") },
                    placeholder = { Text(placeholder) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            } else {
                Text(
                    "Uma chave aleatória será gerada automaticamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            if (salvando) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                }
            } else {
                GradientButton(
                    text = "Criar chave",
                    onClick = {
                        val valor = if (tipoSelecionado == "RANDOM") "auto" else valorChave.trim()
                        if (tipoSelecionado != "RANDOM" && valor.isBlank()) return@GradientButton
                        onSave(tipoSelecionado, valor)
                    },
                    enabled = tipoSelecionado == "RANDOM" || valorChave.isNotBlank()
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
