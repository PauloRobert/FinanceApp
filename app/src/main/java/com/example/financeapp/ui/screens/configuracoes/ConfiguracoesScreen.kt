package com.example.financeapp.ui.screens.configuracoes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.OrigemDados
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesScreen(
    aoVoltar: () -> Unit,
    viewModel: ConfiguracoesViewModel = koinViewModel()
) {
    val origemAtual by viewModel.origemSelecionada.collectAsState()

    // Função que seleciona e redireciona automaticamente
    fun selecionarEVoltar(origem: OrigemDados) {
        viewModel.selecionarOrigem(origem)
        aoVoltar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Origem de Dados",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecione a tecnologia de persistência ativa:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Opção Room
            OpcaoOrigem(
                titulo = "Room (SQLite Local)",
                descricao = "Banco de dados local no dispositivo",
                icone = Icons.Default.Storage,
                selecionada = origemAtual == OrigemDados.ROOM,
                aoSelecionar = { selecionarEVoltar(OrigemDados.ROOM) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opção API REST
            OpcaoOrigem(
                titulo = "API REST (Retrofit)",
                descricao = "Servidor remoto via HTTP",
                icone = Icons.Default.Wifi,
                selecionada = origemAtual == OrigemDados.REMOTE,
                aoSelecionar = { selecionarEVoltar(OrigemDados.REMOTE) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opção Firebase
            OpcaoOrigem(
                titulo = "Firebase Firestore",
                descricao = "Banco de dados em nuvem do Google",
                icone = Icons.Default.Cloud,
                selecionada = origemAtual == OrigemDados.FIREBASE,
                aoSelecionar = { selecionarEVoltar(OrigemDados.FIREBASE) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Indicador visual da tecnologia ativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tecnologia Ativa",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when (origemAtual) {
                            OrigemDados.ROOM -> "Room (SQLite Local)"
                            OrigemDados.REMOTE -> "API REST (Retrofit)"
                            OrigemDados.FIREBASE -> "Firebase Firestore"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcaoOrigem(
    titulo: String,
    descricao: String,
    icone: ImageVector,
    selecionada: Boolean,
    aoSelecionar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selecionada)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selecionada,
                onClick = aoSelecionar
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = if (selecionada)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
