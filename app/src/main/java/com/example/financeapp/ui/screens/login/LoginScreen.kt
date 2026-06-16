package com.example.financeapp.ui.screens.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.components.IFBankLogo
import com.example.financeapp.ui.components.LogoSize
import com.example.financeapp.ui.theme.IFBlue
import com.example.financeapp.ui.theme.IFBlueDark
import com.example.financeapp.ui.theme.IFLilac
import com.example.financeapp.ui.theme.IFLilacLight
import com.example.financeapp.ui.theme.IFPurple
import org.koin.androidx.compose.koinViewModel
import kotlin.math.sin

@Composable
fun LoginScreen(
    aoNavegarParaRegistro: () -> Unit,
    aoLoginRealizado: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var senhaVisivel by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(estado.loginRealizado) {
        if (estado.loginRealizado) {
            aoLoginRealizado()
        }
    }

    LaunchedEffect(estado.mensagemErro) {
        estado.mensagemErro?.let { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
            viewModel.limparErro()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Animated financial waves background
            FinancialWavesBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // Logo IF Bank
                IFBankLogo(
                    size = LogoSize.Large,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Seu banco digital inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Bem-vindo de volta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Faça login para acessar sua conta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo de usuário
                OutlinedTextField(
                    value = estado.nomeUsuario,
                    onValueChange = viewModel::atualizarNomeUsuario,
                    label = { Text("Usuário") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Ícone de usuário",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    enabled = !estado.carregando,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de senha
                OutlinedTextField(
                    value = estado.senha,
                    onValueChange = viewModel::atualizarSenha,
                    label = { Text("Senha") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Ícone de senha",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(
                                imageVector = if (senhaVisivel) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = if (senhaVisivel) "Ocultar senha"
                                else "Mostrar senha",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    enabled = !estado.carregando,
                    visualTransformation = if (senhaVisivel) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botão de login gradiente
                if (estado.carregando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                } else {
                    GradientButton(
                        text = "Entrar",
                        onClick = { viewModel.login() },
                        enabled = !estado.carregando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Link para registro
                TextButton(onClick = aoNavegarParaRegistro) {
                    Text(
                        text = "Não tem conta? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Cadastre-se",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = IFLilacLight
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun FinancialWavesBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave2"
    )
    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave3"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background gradient
        drawRect(
            brush = Brush.verticalGradient(
                listOf(IFBlueDark, Color(0xFF1E1B4B), Color(0xFF0F172A))
            )
        )

        // Wave 1 — large slow wave (blue)
        val path1 = androidx.compose.ui.graphics.Path()
        path1.moveTo(0f, h * 0.35f)
        for (x in 0..w.toInt() step 4) {
            val xf = x.toFloat()
            val y = h * 0.35f + sin((xf / w * 4 + phase1 * Math.PI / 180).toFloat()) * 40f
            path1.lineTo(xf, y)
        }
        path1.lineTo(w, h)
        path1.lineTo(0f, h)
        path1.close()
        drawPath(path1, IFBlue.copy(alpha = 0.08f))

        // Wave 2 — medium wave (purple)
        val path2 = androidx.compose.ui.graphics.Path()
        path2.moveTo(0f, h * 0.5f)
        for (x in 0..w.toInt() step 4) {
            val xf = x.toFloat()
            val y = h * 0.5f + sin((xf / w * 6 + phase2 * Math.PI / 180).toFloat()) * 30f
            path2.lineTo(xf, y)
        }
        path2.lineTo(w, h)
        path2.lineTo(0f, h)
        path2.close()
        drawPath(path2, IFPurple.copy(alpha = 0.06f))

        // Wave 3 — small fast wave (lilac)
        val path3 = androidx.compose.ui.graphics.Path()
        path3.moveTo(0f, h * 0.65f)
        for (x in 0..w.toInt() step 4) {
            val xf = x.toFloat()
            val y = h * 0.65f + sin((xf / w * 8 + phase3 * Math.PI / 180).toFloat()) * 20f
            path3.lineTo(xf, y)
        }
        path3.lineTo(w, h)
        path3.lineTo(0f, h)
        path3.close()
        drawPath(path3, IFLilac.copy(alpha = 0.05f))

        // Floating particles — simulating financial data points
        for (i in 0..15) {
            val px = (w * ((i * 0.0618f + phase1 / 360f) % 1f))
            val py = h * ((i * 0.0732f + phase2 / 720f) % 0.4f) + h * 0.05f
            val radius = 2f + (i % 3) * 1.5f
            drawCircle(
                color = when (i % 3) {
                    0 -> IFBlue.copy(alpha = 0.3f)
                    1 -> IFPurple.copy(alpha = 0.25f)
                    else -> IFLilacLight.copy(alpha = 0.2f)
                },
                radius = radius,
                center = Offset(px, py)
            )
        }
    }
}
