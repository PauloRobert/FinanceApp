package com.example.financeapp.ui.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.ui.theme.IFExpense
import com.example.financeapp.ui.theme.IFExpenseDark
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.ui.theme.IFIncomeDark
import java.math.BigDecimal

@Composable
fun InsightCard(
    titulo: String,
    valor: String,
    icone: ImageVector,
    tipo: InsightType,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val iconColor = when (tipo) {
        InsightType.POSITIVE -> if (isDark) IFIncomeDark else IFIncome
        InsightType.NEGATIVE -> if (isDark) IFExpenseDark else IFExpense
        InsightType.NEUTRAL -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icone,
                contentDescription = titulo,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun InsightsRow(
    totalEntradas: BigDecimal,
    totalSaidas: BigDecimal,
    formatarMoeda: (BigDecimal) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InsightCard(
            titulo = "Receitas do mês",
            valor = formatarMoeda(totalEntradas),
            icone = Icons.AutoMirrored.Filled.TrendingUp,
            tipo = InsightType.POSITIVE,
            modifier = Modifier.weight(1f)
        )
        InsightCard(
            titulo = "Gastos do mês",
            valor = formatarMoeda(totalSaidas),
            icone = Icons.AutoMirrored.Filled.TrendingDown,
            tipo = InsightType.NEGATIVE,
            modifier = Modifier.weight(1f)
        )
    }
}

enum class InsightType { POSITIVE, NEGATIVE, NEUTRAL }
