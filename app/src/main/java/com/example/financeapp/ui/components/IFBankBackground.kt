package com.example.financeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun IFBankBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val gradient = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0F172A),
                Color(0xFF131B2E),
                Color(0xFF151E33)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFEFF3FF),
                Color(0xFFF5F3FF),
                Color(0xFFF8FAFC)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        content = content
    )
}
