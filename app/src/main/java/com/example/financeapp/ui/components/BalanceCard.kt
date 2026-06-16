package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.ui.theme.IFGradientCard
import com.example.financeapp.ui.theme.IFGradientDarkCard
import com.example.financeapp.ui.theme.IFIncome
import java.math.BigDecimal

@Composable
fun BalanceCard(
    saldo: BigDecimal,
    totalEntradas: BigDecimal,
    totalSaidas: BigDecimal,
    saldoVisivel: Boolean,
    aoAlternarVisibilidade: () -> Unit,
    formatarMoeda: (BigDecimal) -> String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val gradient = if (isDark) IFGradientDarkCard else IFGradientCard

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(gradient)
            .padding(24.dp)
    ) {
        // Header do card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saldo disponível",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            IconButton(
                onClick = aoAlternarVisibilidade,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (saldoVisivel) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                    contentDescription = if (saldoVisivel) "Ocultar saldo"
                    else "Mostrar saldo",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Saldo principal
        Text(
            text = if (saldoVisivel) formatarMoeda(saldo) else "R$ ••••••",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Entradas e Saídas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Entradas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Entradas",
                    tint = IFIncome,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Receitas",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (saldoVisivel) formatarMoeda(totalEntradas) else "••••",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = IFIncome
                    )
                }
            }

            // Saídas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = "Saídas",
                    tint = Color(0xFFFCA5A5),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Despesas",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (saldoVisivel) formatarMoeda(totalSaidas) else "••••",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFCA5A5)
                    )
                }
            }
        }
    }
}
