package com.example.financeapp.ui.screens.seguranca

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.financeapp.ui.components.GradientButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegurancaScreen(
    aoVoltar: () -> Unit,
    viewModel: SegurancaViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(estado.mensagemSucesso, estado.mensagemErro) {
        estado.mensagemSucesso?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagens()
        }
        estado.mensagemErro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Segurança",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (estado.carregando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Seção: Alterar Senha
                SectionLabel("Senha")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SecurityIcon(Icons.Default.Key)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        "Alterar senha",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        if (estado.lastPasswordChange != null)
                                            "Alterada recentemente"
                                        else
                                            "Nunca alterada",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TextButton(onClick = {
                                if (estado.mostrarTrocaSenha) viewModel.ocultarTrocaSenha()
                                else viewModel.mostrarTrocaSenha()
                            }) {
                                Text(
                                    if (estado.mostrarTrocaSenha) "Cancelar" else "Alterar",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (estado.mostrarTrocaSenha) {
                            Spacer(modifier = Modifier.height(16.dp))
                            PasswordChangeForm(estado, viewModel)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Seção: Proteção
                SectionLabel("Proteção")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        SecurityToggleItem(
                            icone = Icons.Default.PhonelinkLock,
                            titulo = "Autenticação em dois fatores",
                            subtitulo = "Proteção extra ao fazer login",
                            checked = estado.twoFactorEnabled,
                            onCheckedChange = viewModel::alternarTwoFactor
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        SecurityToggleItem(
                            icone = Icons.Default.Fingerprint,
                            titulo = "Biometria",
                            subtitulo = "Usar digital ou reconhecimento facial",
                            checked = estado.biometricEnabled,
                            onCheckedChange = { ativar ->
                                if (ativar && activity != null) {
                                    val biometricManager = BiometricManager.from(context)
                                    val canAuth = biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                                                or BiometricManager.Authenticators.BIOMETRIC_WEAK
                                    )
                                    if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
                                        viewModel.exibirErro("Biometria não disponível neste dispositivo")
                                        return@SecurityToggleItem
                                    }

                                    val executor = ContextCompat.getMainExecutor(context)
                                    val callback = object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationSucceeded(
                                            result: BiometricPrompt.AuthenticationResult
                                        ) {
                                            viewModel.alternarBiometria(true)
                                        }

                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence
                                        ) {
                                            if (errorCode != BiometricPrompt.ERROR_USER_CANCELED
                                                && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                                            ) {
                                                viewModel.exibirErro("Erro: $errString")
                                            }
                                        }

                                        override fun onAuthenticationFailed() {
                                            viewModel.exibirErro("Biometria não reconhecida")
                                        }
                                    }

                                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                                        .setTitle("IF Bank")
                                        .setSubtitle("Confirme sua identidade para ativar a biometria")
                                        .setNegativeButtonText("Cancelar")
                                        .setAllowedAuthenticators(
                                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                                                    or BiometricManager.Authenticators.BIOMETRIC_WEAK
                                        )
                                        .build()

                                    BiometricPrompt(activity, executor, callback)
                                        .authenticate(promptInfo)
                                } else {
                                    viewModel.alternarBiometria(false)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Seção: Notificações de segurança
                SectionLabel("Alertas")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    SecurityToggleItem(
                        icone = Icons.Default.Notifications,
                        titulo = "Notificação de login",
                        subtitulo = "Ser avisado em novos acessos",
                        checked = estado.loginNotificationEnabled,
                        onCheckedChange = viewModel::alternarNotificacaoLogin
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PasswordChangeForm(
    estado: SegurancaUiState,
    viewModel: SegurancaViewModel
) {
    var senhaAtualVisivel by remember { mutableStateOf(false) }
    var novaSenhaVisivel by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    OutlinedTextField(
        value = estado.senhaAtual,
        onValueChange = viewModel::atualizarSenhaAtual,
        label = { Text("Senha atual") },
        leadingIcon = {
            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingIcon = {
            IconButton(onClick = { senhaAtualVisivel = !senhaAtualVisivel }) {
                Icon(
                    if (senhaAtualVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (senhaAtualVisivel) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = textFieldColors
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = estado.novaSenha,
        onValueChange = viewModel::atualizarNovaSenha,
        label = { Text("Nova senha") },
        leadingIcon = {
            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingIcon = {
            IconButton(onClick = { novaSenhaVisivel = !novaSenhaVisivel }) {
                Icon(
                    if (novaSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (novaSenhaVisivel) VisualTransformation.None
        else PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        supportingText = { Text("Mínimo 6 caracteres") },
        colors = textFieldColors
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = estado.confirmarNovaSenha,
        onValueChange = viewModel::atualizarConfirmarNovaSenha,
        label = { Text("Confirmar nova senha") },
        leadingIcon = {
            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary)
        },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = textFieldColors
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (estado.trocandoSenha) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    } else {
        GradientButton(
            text = "Confirmar alteração",
            onClick = { viewModel.alterarSenha() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SecurityToggleItem(
    icone: ImageVector,
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecurityIcon(icone)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
private fun SecurityIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SectionLabel(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}
