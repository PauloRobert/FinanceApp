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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.utils.formatCurrency
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixEnviarScreen(
    aoVoltar: () -> Unit,
    chaveInicial: String? = null,
    viewModel: PixEnviarViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LaunchedEffect(chaveInicial) {
        if (!chaveInicial.isNullOrBlank()) {
            viewModel.definirChaveInicial(chaveInicial)
        }
    }

    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    LaunchedEffect(estado.mensagemSucesso) {
        estado.mensagemSucesso?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    val titulo = when (estado.etapa) {
        PixEnviarEtapa.BUSCA -> "Enviar Pix"
        PixEnviarEtapa.VALOR -> "Valor"
        PixEnviarEtapa.CONFIRMACAO -> "Confirmar Pix"
        PixEnviarEtapa.COMPROVANTE -> "Comprovante"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    if (estado.etapa != PixEnviarEtapa.COMPROVANTE) {
                        IconButton(onClick = {
                            if (estado.etapa == PixEnviarEtapa.BUSCA) aoVoltar()
                            else viewModel.voltarEtapa()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when (estado.etapa) {
                PixEnviarEtapa.BUSCA -> EtapaBusca(estado, viewModel)
                PixEnviarEtapa.VALOR -> EtapaValor(estado, viewModel, formatador)
                PixEnviarEtapa.CONFIRMACAO -> EtapaConfirmacao(estado, viewModel, formatador)
                PixEnviarEtapa.COMPROVANTE -> EtapaComprovante(estado, viewModel, formatador, aoVoltar)
            }
        }
    }
}

@Composable
private fun EtapaBusca(estado: PixEnviarUiState, viewModel: PixEnviarViewModel) {
    Spacer(modifier = Modifier.height(24.dp))

    Text(
        "Para quem você quer enviar?",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        "Insira a chave Pix do destinatário",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = estado.chaveDestino,
        onValueChange = viewModel::atualizarChave,
        label = { Text("Chave Pix") },
        placeholder = { Text("CPF, e-mail, telefone ou chave aleatória") },
        leadingIcon = {
            Icon(Icons.Default.Pix, null, tint = MaterialTheme.colorScheme.primary)
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (estado.buscando) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        }
    } else {
        GradientButton(
            text = "Continuar",
            onClick = { viewModel.buscarChave() },
            enabled = estado.chaveDestino.isNotBlank()
        )
    }
}

@Composable
private fun EtapaValor(estado: PixEnviarUiState, viewModel: PixEnviarViewModel, formatador: NumberFormat) {
    Spacer(modifier = Modifier.height(16.dp))

    // Destinatário card
    estado.destinatario?.let { dest ->
        DestinatarioCard(dest.ownerName, dest.bank)
        Spacer(modifier = Modifier.height(24.dp))
    }

    Text(
        "Qual o valor?",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(16.dp))

    val amountField = remember(estado.valor) {
        val formatted = formatCurrency(estado.valor)
        TextFieldValue(text = formatted, selection = TextRange(formatted.length))
    }

    OutlinedTextField(
        value = amountField,
        onValueChange = { newValue ->
            val digits = newValue.text.replace("\\D".toRegex(), "")
            viewModel.atualizarValor(digits)
        },
        label = { Text("Valor") },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = estado.descricao,
        onValueChange = viewModel::atualizarDescricao,
        label = { Text("Descrição (opcional)") },
        placeholder = { Text("Ex: Almoço, Aluguel...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )

    Spacer(modifier = Modifier.height(32.dp))

    GradientButton(
        text = "Continuar",
        onClick = { viewModel.avancarParaConfirmacao() },
        enabled = (estado.valor.toLongOrNull() ?: 0L) > 0
    )
}

@Composable
private fun EtapaConfirmacao(estado: PixEnviarUiState, viewModel: PixEnviarViewModel, formatador: NumberFormat) {
    val valorDouble = (estado.valor.toLongOrNull() ?: 0L) / 100.0

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        "Confirme os dados",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(modifier = Modifier.height(20.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            ResumoItem("Destinatário", estado.destinatario?.ownerName ?: "")
            ResumoItem("Instituição", estado.destinatario?.bank ?: "IF Bank")
            ResumoItem("Chave", estado.chaveDestino)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            ResumoItem("Valor", formatador.format(valorDouble))
            if (estado.descricao.isNotBlank()) {
                ResumoItem("Descrição", estado.descricao)
            }
            ResumoItem("Data", "Agora")
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    if (estado.enviando) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        }
    } else {
        GradientButton(
            text = "Confirmar Pix",
            onClick = { viewModel.confirmarPix() }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = { viewModel.voltarEtapa() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun EtapaComprovante(
    estado: PixEnviarUiState,
    viewModel: PixEnviarViewModel,
    formatador: NumberFormat,
    aoVoltar: () -> Unit
) {
    val comp = estado.comprovante ?: return

    Spacer(modifier = Modifier.height(24.dp))

    // Ícone sucesso
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(IFIncome.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CheckCircle,
                "Sucesso",
                tint = IFIncome,
                modifier = Modifier.size(40.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        "Pix enviado!",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = IFIncome,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        formatador.format(comp.amount),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            ResumoItem("Destinatário", comp.receiverName ?: "")
            ResumoItem("Instituição", comp.receiverBank ?: "IF Bank")
            ResumoItem("Chave", comp.receiverKey)
            if (!comp.description.isNullOrBlank()) {
                ResumoItem("Descrição", comp.description)
            }
            ResumoItem("ID", comp.id.take(8) + "...")
            ResumoItem("Status", comp.status)
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    GradientButton(
        text = "Novo Pix",
        onClick = { viewModel.novoPix() }
    )
    Spacer(modifier = Modifier.height(12.dp))
    TextButton(
        onClick = aoVoltar,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Voltar ao início", color = MaterialTheme.colorScheme.primary)
    }

    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun DestinatarioCard(nome: String, banco: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(nome, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(banco, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ResumoItem(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
