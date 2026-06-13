package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
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
    val color = if (transaction.type == TransactionType.INCOME) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val icon = if (transaction.type == TransactionType.INCOME) {
        Icons.Default.ArrowUpward
    } else {
        Icons.Default.ArrowDownward
    }

    val dissmissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dissmissState.progress) {

        val process = dissmissState.progress

        if (process > 0.9f) {
            when(dissmissState.targetValue){
                SwipeToDismissBoxValue.StartToEnd -> {
                    //Edit
                    onEdit(transaction)

                }

                SwipeToDismissBoxValue.EndToStart -> {
                    //Delete
                    onDelete(transaction)

                }

                else -> {}
            }
            dissmissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dissmissState,
        backgroundContent = {
            val target = dissmissState.targetValue

            val process = dissmissState.progress

            val color = when (target) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF1976D2).copy(alpha = process)
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFD32F2F).copy(alpha = process)
                else -> Color.Transparent
            }

            val icon = when (target) {
                SwipeToDismissBoxValue.StartToEnd -> "✏️"
                SwipeToDismissBoxValue.EndToStart -> "🗑️"
                else -> ""
            }

            val scale = 0.8f + (process * 0.4f)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(16.dp),
                horizontalArrangement = when (target) {
                    SwipeToDismissBoxValue.StartToEnd -> Arrangement.Start
                    SwipeToDismissBoxValue.EndToStart -> Arrangement.End
                    else -> Arrangement.Start
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 20.sp, modifier = Modifier.scale(scale))
            }
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        )
        {
            Column(
                modifier = Modifier
                    .height(80.dp)
                    .padding(8.dp)
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row() {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color
                        )
                        Text(formatter.format(transaction.date), fontSize = 14.sp)
                    }

                    Text(
                        formatCurrencyBr(transaction.amount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    transaction.description,
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }


}