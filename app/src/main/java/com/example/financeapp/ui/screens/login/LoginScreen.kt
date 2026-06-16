package com.example.financeapp.ui.screens.login

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
import com.example.financeapp.ui.theme.IFGradientSubtle
import org.koin.androidx.compose.koinViewModel

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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Gradient accent at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(IFGradientSubtle)
            )

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
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Seu banco digital inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Bem-vindo de volta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Faça login para acessar sua conta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Cadastre-se",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
