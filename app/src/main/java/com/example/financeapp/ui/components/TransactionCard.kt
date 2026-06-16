package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.ui.theme.IFExpense
import com.example.financeapp.ui.theme.IFExpenseDark
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.ui.theme.IFIncomeDark
import com.example.financeapp.utils.formatCurrencyBr
import java.time.format.DateTimeFormatter

@Composable
fun TransactionCard(
    transaction: Transaction,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    modifier: Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val isDark = isSystemInDarkTheme()

    val isIncome = transaction.type == TransactionType.INCOME
    val accentColor = if (isIncome) {
        if (isDark) IFIncomeDark else IFIncome
    } else {
        if (isDark) IFExpenseDark else IFExpense
    }
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val prefix = if (isIncome) "+ " else "- "

    var mostrarMenu by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.progress) {
        val process = dismissState.progress
        if (process > 0.9f) {
            when (dismissState.targetValue) {
                SwipeToDismissBoxValue.StartToEnd -> onEdit(transaction)
                SwipeToDismissBoxValue.EndToStart -> onDelete(transaction)
                else -> {}
            }
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val target = dismissState.targetValue
            val process = dismissState.progress

            val bgColor = when (target) {
                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary.copy(alpha = process)
                SwipeToDismissBoxValue.EndToStart -> IFExpense.copy(alpha = process)
                else -> Color.Transparent
            }

            val swipeIcon = when (target) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> null
            }

            val scale = 0.8f + (process * 0.4f)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(bgColor)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = when (target) {
                    SwipeToDismissBoxValue.StartToEnd -> Arrangement.Start
                    SwipeToDismissBoxValue.EndToStart -> Arrangement.End
                    else -> Arrangement.Start
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                swipeIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale)
                    )
                }
            }
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { mostrarMenu = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone circular
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Descrição e data
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatter.format(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Valor
                Text(
                    text = "$prefix${formatCurrencyBr(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
            }

            // Menu dropdown
            DropdownMenu(
                expanded = mostrarMenu,
                onDismissRequest = { mostrarMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = {
                        mostrarMenu = false
                        onEdit(transaction)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Excluir") },
                    onClick = {
                        mostrarMenu = false
                        onDelete(transaction)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}