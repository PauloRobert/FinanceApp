package com.example.financeapp.ui.screens.transferencia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.remote.dto.BeneficiaryDto
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.utils.formatCurrency
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferenciaEnviarScreen(
    aoVoltar: () -> Unit,
    favorecidoId: String? = null,
    viewModel: TransferenciaEnviarViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    LaunchedEffect(favorecidoId) {
        if (!favorecidoId.isNullOrBlank()) viewModel.preCarregarFavorecido(favorecidoId)
    }

    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let { snackbarHostState.showSnackbar(it); viewModel.limparErro() }
    }

    val titulo = when (estado.etapa) {
        TransferenciaEtapa.DESTINATARIO -> "Transferir"
        TransferenciaEtapa.VALOR -> "Valor"
        TransferenciaEtapa.CONFIRMACAO -> "Confirmar"
        TransferenciaEtapa.COMPROVANTE -> "Comprovante"
    }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (estado.etapa != TransferenciaEtapa.COMPROVANTE) {
                        IconButton(onClick = { if (estado.etapa == TransferenciaEtapa.DESTINATARIO) aoVoltar() else viewModel.voltarEtapa() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when (estado.etapa) {
            TransferenciaEtapa.DESTINATARIO -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    // Favorecidos
                    if (estado.favorecidos.isNotEmpty()) {
                        Text(
                            "Favorecidos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filtered = if (estado.buscaFavorecido.isBlank()) estado.favorecidos
                            else estado.favorecidos.filter {
                                it.name.contains(estado.buscaFavorecido, true) || it.nickname?.contains(estado.buscaFavorecido, true) == true
                            }
                            items(filtered, key = { it.id }) { fav ->
                                FavorecidoCard(fav) { viewModel.selecionarFavorecido(fav) }
                            }
                        }
                    }

                    // Manual
                    Column(Modifier.padding(24.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                        Text("Ou preencha manualmente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(estado.nomeDestinatario, viewModel::atualizarNome, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(estado.bancoDestinatario, viewModel::atualizarBanco, label = { Text("Banco") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(estado.agenciaDestinatario, viewModel::atualizarAgencia, label = { Text("Agência") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                            OutlinedTextField(estado.contaDestinatario, viewModel::atualizarConta, label = { Text("Conta") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("CORRENTE", "POUPANCA").forEach { tipo ->
                                FilterChip(selected = estado.tipoConta == tipo, onClick = { viewModel.atualizarTipoConta(tipo) },
                                    label = { Text(if (tipo == "CORRENTE") "Corrente" else "Poupança") })
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        GradientButton("Continuar", onClick = { viewModel.continuarManual() })
                    }
                }
            }

            TransferenciaEtapa.VALOR -> {
                Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
                    Spacer(Modifier.height(16.dp))
                    DestinatarioResumo(estado)
                    Spacer(Modifier.height(24.dp))
                    Text("Qual o valor?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    val amountField = remember(estado.valor) {
                        val f = formatCurrency(estado.valor); TextFieldValue(f, TextRange(f.length))
                    }
                    OutlinedTextField(amountField, { viewModel.atualizarValor(it.text.replace("\\D".toRegex(), "")) }, label = { Text("Valor") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = tfColors, textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(estado.descricao, viewModel::atualizarDescricao, label = { Text("Descrição (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true, colors = tfColors)
                    Spacer(Modifier.height(32.dp))
                    GradientButton("Continuar", onClick = { viewModel.avancarParaConfirmacao() }, enabled = (estado.valor.toLongOrNull() ?: 0L) > 0)
                }
            }

            TransferenciaEtapa.CONFIRMACAO -> {
                val valorDouble = (estado.valor.toLongOrNull() ?: 0L) / 100.0
                Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
                    Spacer(Modifier.height(16.dp))
                    Text("Confirme os dados", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(20.dp))
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(1.dp)) {
                        Column(Modifier.padding(20.dp)) {
                            ResumoLinha("Destinatário", estado.nomeDestinatario)
                            ResumoLinha("Banco", estado.bancoDestinatario)
                            ResumoLinha("Conta", "${estado.agenciaDestinatario} / ${estado.contaDestinatario}")
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ResumoLinha("Valor", fmt.format(valorDouble))
                            if (estado.descricao.isNotBlank()) ResumoLinha("Descrição", estado.descricao)
                            ResumoLinha("Data", "Agora")
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    if (estado.enviando) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) }
                    } else {
                        GradientButton("Confirmar transferência", onClick = { viewModel.confirmarTransferencia() })
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.voltarEtapa() }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }

            TransferenciaEtapa.COMPROVANTE -> {
                val c = estado.comprovante ?: return@Scaffold
                Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.size(72.dp).clip(CircleShape).background(IFIncome.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, "Sucesso", tint = IFIncome, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Transferência realizada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = IFIncome, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(fmt.format(c.amount), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(1.dp)) {
                        Column(Modifier.padding(20.dp)) {
                            ResumoLinha("Destinatário", c.receiverName)
                            ResumoLinha("Banco", c.receiverBank)
                            ResumoLinha("Conta", c.receiverAccount)
                            if (!c.description.isNullOrBlank()) ResumoLinha("Descrição", c.description)
                            ResumoLinha("ID", c.id.take(8) + "...")
                            ResumoLinha("Status", c.status)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    GradientButton("Nova transferência", onClick = { viewModel.novaTransferencia() })
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = aoVoltar, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao início", color = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FavorecidoCard(fav: BeneficiaryDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(fav.nickname ?: fav.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${fav.bankName} • ****${fav.account.takeLast(4)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DestinatarioResumo(estado: TransferenciaEnviarUiState) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(estado.nomeDestinatario, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("${estado.bancoDestinatario} • ****${estado.contaDestinatario.takeLast(4)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ResumoLinha(label: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
