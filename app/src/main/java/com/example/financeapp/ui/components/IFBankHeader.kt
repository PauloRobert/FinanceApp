package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun IFBankHeader(
    nomeUsuario: String,
    aoAbrirConfiguracoes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val saudacao = when (LocalTime.now().hour) {
        in 0..5 -> "Boa madrugada"
        in 6..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = saudacao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (nomeUsuario.isNotBlank()) {
                    Text(
                        text = nomeUsuario,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Ações do header
        Row {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = aoAbrirConfiguracoes) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Configurações",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
