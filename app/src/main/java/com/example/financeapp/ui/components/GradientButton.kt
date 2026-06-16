package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.ui.theme.IFGradientPrimary

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = IFGradientPrimary,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (enabled) gradient
                else Brush.linearGradient(
                    listOf(Color(0xFF94A3B8), Color(0xFF94A3B8))
                )
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
