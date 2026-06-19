package com.example.financeapp.ui.screens.extrato

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.components.IFBankBackground
import com.example.financeapp.ui.theme.*
import com.example.financeapp.utils.formatCurrencyBr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

// ── State ──────────────────────────────────────────────
data class ExtratoUiState(
    val carregando: Boolean = false,
    val transacoes: List<Transaction> = emptyList(),
    val filtro: String = "all",
    val exportando: Boolean = false,
    val mensagem: String? = null
)

// ── ViewModel ──────────────────────────────────────────
class ExtratoViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(ExtratoUiState())
    val estado: StateFlow<ExtratoUiState> = _estado.asStateFlow()

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            transactionRepository.obterTransacoes()
                .catch { erro ->
                    android.util.Log.e("FinanceDebug", "Extrato ERRO ao carregar: ${erro.message}", erro)
                    _estado.update {
                        it.copy(
                            carregando = false,
                            mensagem = "Erro ao carregar extrato: ${erro.message}"
                        )
                    }
                }
                .collect { transacoes ->
                    _estado.update { it.copy(carregando = false, transacoes = transacoes) }
                }
        }
    }

    fun filtrar(filtro: String) {
        _estado.update { it.copy(filtro = filtro) }
    }

    fun getTransacoesFiltradas(): List<Transaction> {
        val state = _estado.value
        return when (state.filtro) {
            "income" -> state.transacoes.filter { it.type == TransactionType.INCOME }
            "expense" -> state.transacoes.filter { it.type == TransactionType.EXPENSE }
            else -> state.transacoes
        }
    }

    fun gerarPdf(context: Context) {
        viewModelScope.launch {
            _estado.update { it.copy(exportando = true) }
            try {
                val transacoes = getTransacoesFiltradas()
                if (transacoes.isEmpty()) {
                    _estado.update { it.copy(exportando = false, mensagem = "Nenhuma transação para exportar") }
                    return@launch
                }

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val currencyFmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                val doc = PdfDocument()
                val pw = 595
                val ph = 842
                var pageNum = 1
                val marginL = 40f
                val marginR = pw - 40f
                val contentW = marginR - marginL

                // Cores
                val azulPrimario = android.graphics.Color.rgb(37, 99, 235)
                val azulEscuro = android.graphics.Color.rgb(30, 58, 138)
                val roxo = android.graphics.Color.rgb(124, 58, 237)
                val cinzaClaro = android.graphics.Color.rgb(241, 245, 249)
                val cinzaBorda = android.graphics.Color.rgb(203, 213, 225)
                val textoEscuro = android.graphics.Color.rgb(30, 41, 59)
                val textoMedio = android.graphics.Color.rgb(100, 116, 139)
                val verde = android.graphics.Color.rgb(16, 185, 129)
                val vermelho = android.graphics.Color.rgb(239, 68, 68)

                // Paints
                val pBrand = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 24f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = android.graphics.Color.WHITE }
                val pBrandSub = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 10f; color = android.graphics.Color.argb(180, 255, 255, 255) }
                val pTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 16f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = textoEscuro }
                val pSubtitle = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; color = textoMedio }
                val pHeader = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = android.graphics.Color.WHITE }
                val pBody = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; color = textoEscuro }
                val pBodyBold = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = textoEscuro }
                val pIncome = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = verde }
                val pExpense = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = vermelho }
                val pSummaryLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 10f; color = textoMedio }
                val pSummaryValue = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 14f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = textoEscuro }
                val pLine = Paint().apply { color = cinzaBorda; strokeWidth = 0.5f }
                val pBg = Paint().apply { color = cinzaClaro }
                val pHeaderBg = Paint().apply { color = azulPrimario }
                val pFooter = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 7f; color = textoMedio; textAlign = Paint.Align.CENTER }

                // Cálculos
                val totalEntradas = transacoes.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val totalSaidas = transacoes.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                val saldo = totalEntradas - totalSaidas

                fun newPage(): PdfDocument.Page {
                    val info = PdfDocument.PageInfo.Builder(pw, ph, pageNum++).create()
                    return doc.startPage(info)
                }

                var page = newPage()
                var canvas = page.canvas
                var y = 0f

                // ═══ HEADER BANNER ═══
                val headerPaint = Paint().apply {
                    shader = android.graphics.LinearGradient(0f, 0f, pw.toFloat(), 80f, azulEscuro, roxo, android.graphics.Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, 0f, pw.toFloat(), 80f, headerPaint)
                canvas.drawText("IF Bank", marginL, 35f, pBrand)
                canvas.drawText("Extrato Bancário", marginL, 55f, pBrandSub)
                canvas.drawText("Gerado em ${java.time.LocalDateTime.now().format(formatter)}", marginL, 70f, pBrandSub)

                // Data no canto direito
                val pDateRight = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f; color = android.graphics.Color.argb(200, 255, 255, 255); textAlign = Paint.Align.RIGHT }
                canvas.drawText("Documento confidencial", marginR, 35f, pDateRight)
                canvas.drawText("IF Bank S.A. - CNPJ: 00.000.000/0001-00", marginR, 50f, pDateRight)

                y = 100f

                // ═══ RESUMO CARDS ═══
                val cardW = contentW / 3 - 6f
                // Entradas
                canvas.drawRect(marginL, y, marginL + cardW, y + 50f, pBg)
                canvas.drawText("Entradas", marginL + 8f, y + 15f, pSummaryLabel)
                pSummaryValue.color = verde
                canvas.drawText(currencyFmt.format(totalEntradas), marginL + 8f, y + 38f, pSummaryValue)

                // Saídas
                val card2X = marginL + cardW + 8f
                canvas.drawRect(card2X, y, card2X + cardW, y + 50f, pBg)
                canvas.drawText("Saídas", card2X + 8f, y + 15f, pSummaryLabel)
                pSummaryValue.color = vermelho
                canvas.drawText(currencyFmt.format(totalSaidas), card2X + 8f, y + 38f, pSummaryValue)

                // Saldo
                val card3X = card2X + cardW + 8f
                canvas.drawRect(card3X, y, card3X + cardW, y + 50f, pBg)
                canvas.drawText("Saldo", card3X + 8f, y + 15f, pSummaryLabel)
                pSummaryValue.color = if (saldo >= java.math.BigDecimal.ZERO) verde else vermelho
                canvas.drawText(currencyFmt.format(saldo), card3X + 8f, y + 38f, pSummaryValue)

                y += 65f

                // ═══ TÍTULO SEÇÃO ═══
                canvas.drawText("Movimentações (${transacoes.size})", marginL, y, pTitle)
                y += 20f

                // ═══ TABLE HEADER ═══
                canvas.drawRect(marginL, y - 12f, marginR, y + 6f, pHeaderBg)
                canvas.drawText("DATA", marginL + 6f, y, pHeader)
                canvas.drawText("DESCRIÇÃO", marginL + 110f, y, pHeader)
                canvas.drawText("TIPO", marginL + 340f, y, pHeader)
                canvas.drawText("VALOR", marginL + 420f, y, pHeader)
                y += 16f

                // ═══ TABLE ROWS ═══
                var isAlt = false
                for (tx in transacoes) {
                    if (y > ph - 50f) {
                        // Footer na página atual
                        canvas.drawLine(marginL, ph - 35f, marginR, ph - 35f, pLine)
                        canvas.drawText("IF Bank — Extrato Bancário — Página ${pageNum - 1}", pw / 2f, ph - 20f, pFooter)
                        doc.finishPage(page)
                        page = newPage()
                        canvas = page.canvas
                        y = 40f
                        isAlt = false
                    }

                    // Fundo alternado
                    if (isAlt) canvas.drawRect(marginL, y - 11f, marginR, y + 5f, pBg)
                    isAlt = !isAlt

                    val tipo = if (tx.type == TransactionType.INCOME) "Entrada" else "Saída"
                    val paint = if (tx.type == TransactionType.INCOME) pIncome else pExpense
                    val prefix = if (tx.type == TransactionType.INCOME) "+ " else "- "

                    canvas.drawText(tx.date.format(formatter), marginL + 6f, y, pBody)
                    canvas.drawText(tx.description.take(35), marginL + 110f, y, pBody)
                    canvas.drawText(tipo, marginL + 340f, y, pBody)
                    canvas.drawText("$prefix${currencyFmt.format(tx.amount)}", marginL + 420f, y, paint)
                    y += 16f
                }

                // ═══ FOOTER ═══
                y = maxOf(y + 20f, ph - 50f)
                canvas.drawLine(marginL, ph - 35f, marginR, ph - 35f, pLine)
                canvas.drawText("IF Bank — Extrato Bancário — Página ${pageNum - 1}", pw / 2f, ph - 20f, pFooter)

                doc.finishPage(page)

                // Salvar em Dispatchers.IO para não bloquear a Main thread
                val file = withContext(Dispatchers.IO) {
                    val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        ?: throw IllegalStateException("Armazenamento externo indisponível")
                    val dir = File(baseDir, "IF Bank")
                    if (!dir.exists() && !dir.mkdirs()) {
                        throw IllegalStateException("Não foi possível criar diretório: ${dir.absolutePath}")
                    }
                    val pdfFile = File(dir, "extrato_ifbank_${System.currentTimeMillis()}.pdf")
                    pdfFile.outputStream().use { doc.writeTo(it) }
                    doc.close()
                    pdfFile
                }

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Extrato IF Bank")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartilhar extrato"))

                _estado.update { it.copy(exportando = false, mensagem = "PDF exportado!") }
            } catch (e: Exception) {
                android.util.Log.e("FinanceDebug", "Extrato ERRO ao exportar PDF", e)
                _estado.update { it.copy(exportando = false, mensagem = "Erro ao exportar: ${e.message}") }
            }
        }
    }

    fun limparMsg() { _estado.update { it.copy(mensagem = null) } }
}

// ── Screen ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(
    aoVoltar: () -> Unit,
    viewModel: ExtratoViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val fmt = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val transacoesFiltradas = viewModel.getTransacoesFiltradas()

    val totalEntradas = transacoesFiltradas.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalSaidas = transacoesFiltradas.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    LaunchedEffect(estado.mensagem) { estado.mensagem?.let { snackbar.showSnackbar(it); viewModel.limparMsg() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Extrato", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${transacoesFiltradas.size} movimentações", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        IFBankBackground {
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Resumo card
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = IFIncome.copy(alpha = 0.1f)), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Entradas", style = MaterialTheme.typography.labelSmall, color = IFIncome)
                            Text(formatCurrencyBr(totalEntradas), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = IFIncome)
                        }
                    }
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = IFExpense.copy(alpha = 0.1f)), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Saídas", style = MaterialTheme.typography.labelSmall, color = IFExpense)
                            Text(formatCurrencyBr(totalSaidas), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = IFExpense)
                        }
                    }
                }

                // Filtros + Exportar
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("all" to "Todos", "income" to "Entradas", "expense" to "Saídas").forEach { (key, label) ->
                            FilterChip(selected = estado.filtro == key, onClick = { viewModel.filtrar(key) }, label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Botão exportar
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    if (estado.exportando) {
                        CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.gerarPdf(context) },
                            shape = MaterialTheme.shapes.medium,
                            enabled = transacoesFiltradas.isNotEmpty()
                        ) {
                            Icon(Icons.Default.FileDownload, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Exportar PDF")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (estado.carregando) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                } else if (transacoesFiltradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Receipt, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            Spacer(Modifier.height(16.dp))
                            Text("Nenhuma movimentação", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(transacoesFiltradas, key = { it.id }) { tx ->
                            val isIncome = tx.type == TransactionType.INCOME
                            val isDark = isSystemInDarkTheme()
                            val color = if (isIncome) { if (isDark) IFIncomeDark else IFIncome } else { if (isDark) IFExpenseDark else IFExpense }

                            Card(
                                Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                            null, tint = color, modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(tx.description, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(tx.date.format(dateFmt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        (if (isIncome) "+ " else "- ") + formatCurrencyBr(tx.amount),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold, color = color
                                    )
                                }
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}
