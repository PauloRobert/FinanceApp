package com.example.financeapp.ui.screens.notificacoes

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ── State ──────────────────────────────────────────────
data class NotificacoesUiState(
    val carregando: Boolean = false,
    val notificacoes: List<Notificacao> = emptyList(),
    val mensagemErro: String? = null
)

data class Notificacao(
    val id: String,
    val titulo: String,
    val mensagem: String,
    val icone: TipoNotificacao,
    val data: LocalDateTime
)

enum class TipoNotificacao {
    ENTRADA, SAIDA, SISTEMA
}

// ── ViewModel ──────────────────────────────────────────
class NotificacoesViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(NotificacoesUiState())
    val estado: StateFlow<NotificacoesUiState> = _estado.asStateFlow()

    init { carregar() }

    private fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true, mensagemErro = null) }
            transactionRepository.obterTransacoes()
                .catch { erro ->
                    _estado.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = "Erro ao carregar notificações: ${erro.message}"
                        )
                    }
                }
                .collect { transacoes ->
                    val notificacoes = gerarNotificacoes(transacoes)
                    _estado.update {
                        it.copy(carregando = false, notificacoes = notificacoes)
                    }
                }
        }
    }

    private fun gerarNotificacoes(transacoes: List<Transaction>): List<Notificacao> {
        val lista = mutableListOf<Notificacao>()

        // Gerar notificações a partir das transações recentes
        transacoes.sortedByDescending { it.date }.take(20).forEach { tx ->
            val tipo = if (tx.type == TransactionType.INCOME) TipoNotificacao.ENTRADA else TipoNotificacao.SAIDA
            val titulo = if (tx.type == TransactionType.INCOME) "Entrada recebida" else "Pagamento realizado"
            val prefixo = if (tx.type == TransactionType.INCOME) "+" else "-"
            lista.add(
                Notificacao(
                    id = "tx_${tx.id}",
                    titulo = titulo,
                    mensagem = "${tx.description} • $prefixo ${formatCurrencyBr(tx.amount)}",
                    icone = tipo,
                    data = tx.date
                )
            )
        }

        // Notificação de boas-vindas (sempre por último)
        lista.add(
            Notificacao(
                id = "welcome",
                titulo = "Bem-vindo ao IF Bank!",
                mensagem = "Sua conta está ativa e pronta para uso.",
                icone = TipoNotificacao.SISTEMA,
                data = LocalDateTime.now().minusDays(30)
            )
        )

        return lista
    }

    fun limparErro() { _estado.update { it.copy(mensagemErro = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacoesScreen(
    aoVoltar: () -> Unit,
    viewModel: NotificacoesViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let {
            snackbar.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Notificações",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${estado.notificacoes.size} notificações",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        IFBankBackground {
            if (estado.carregando) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (estado.notificacoes.isEmpty()) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NotificationsOff,
                            null,
                            Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Nenhuma notificação",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(Modifier.height(4.dp)) }

                    items(estado.notificacoes, key = { it.id }) { notificacao ->
                        NotificacaoItem(notificacao, dateFmt)
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificacaoItem(notificacao: Notificacao, dateFmt: DateTimeFormatter) {
    val (icone, corIcone, corFundo) = when (notificacao.icone) {
        TipoNotificacao.ENTRADA -> Triple(
            Icons.Default.ArrowUpward, IFIncome, IFIncome.copy(alpha = 0.12f)
        )
        TipoNotificacao.SAIDA -> Triple(
            Icons.Default.ArrowDownward, IFExpense, IFExpense.copy(alpha = 0.12f)
        )
        TipoNotificacao.SISTEMA -> Triple(
            Icons.Default.Info,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    }

    val tempoRelativo = formatarTempoRelativo(notificacao.data)

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(corFundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, null, tint = corIcone, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    notificacao.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    notificacao.mensagem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                tempoRelativo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatarTempoRelativo(data: LocalDateTime): String {
    val agora = LocalDateTime.now()
    val minutos = ChronoUnit.MINUTES.between(data, agora)
    val horas = ChronoUnit.HOURS.between(data, agora)
    val dias = ChronoUnit.DAYS.between(data, agora)

    return when {
        minutos < 1 -> "Agora"
        minutos < 60 -> "${minutos}min"
        horas < 24 -> "${horas}h"
        dias < 7 -> "${dias}d"
        dias < 30 -> "${dias / 7}sem"
        else -> "${dias / 30}m"
    }
}
