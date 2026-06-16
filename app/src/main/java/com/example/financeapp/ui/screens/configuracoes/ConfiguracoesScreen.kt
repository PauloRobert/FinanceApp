package com.example.financeapp.ui.screens.configuracoes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.ui.components.IFBankLogo
import com.example.financeapp.ui.components.LogoSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    aoVoltar: () -> Unit,
    aoLogout: () -> Unit = {},
    aoAbrirPerfil: () -> Unit = {},
    aoAbrirSeguranca: () -> Unit = {},
    aoAbrirPrivacidade: () -> Unit = {},
    viewModel: ConfiguracoesViewModel = koinViewModel()
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configurações",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Seção Conta
            SettingsSection(titulo = "Conta") {
                SettingsItem(
                    icone = Icons.Default.Person,
                    titulo = "Perfil",
                    subtitulo = "Dados pessoais e conta",
                    onClick = aoAbrirPerfil
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SettingsItem(
                    icone = Icons.Default.Security,
                    titulo = "Segurança",
                    subtitulo = "Senha e autenticação",
                    onClick = aoAbrirSeguranca
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção Sistema
            SettingsSection(titulo = "Sistema") {
                SettingsItem(
                    icone = Icons.Default.Cloud,
                    titulo = "Fonte de dados",
                    subtitulo = "API REST • Servidor remoto via HTTPS"
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SettingsItem(
                    icone = Icons.Default.Shield,
                    titulo = "Privacidade",
                    subtitulo = "Dados e permissões",
                    onClick = aoAbrirPrivacidade
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção Sobre
            SettingsSection(titulo = "Sobre") {
                SettingsItem(
                    icone = Icons.Default.Info,
                    titulo = "Versão do app",
                    subtitulo = "1.0.0"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de logout
            Button(
                onClick = { viewModel.logout { aoLogout() } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Sair da Conta",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logo IF Bank no rodapé
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IFBankLogo(
                    size = LogoSize.Small,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    titulo: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icone: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icone,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
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
        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

