package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        QuickAction("Pix", Icons.Default.Pix),
        QuickAction("Transferir", Icons.Default.SwapHoriz),
        QuickAction("Depositar", Icons.Default.AccountBalance),
        QuickAction("Pagar", Icons.Default.Receipt),
        QuickAction("Cartões", Icons.Default.CreditCard),
        QuickAction("Investir", Icons.Default.Savings)
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(actions) { action ->
            QuickActionButton(action)
        }
    }
}

@Composable
fun QuickActionButton(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { action.onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = action.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
