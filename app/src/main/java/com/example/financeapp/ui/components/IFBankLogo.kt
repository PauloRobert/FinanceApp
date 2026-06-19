package com.example.financeapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IFBankLogo(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: LogoSize = LogoSize.Medium
) {
    val (fontSize, subtitleSize) = when (size) {
        LogoSize.Small -> 20.sp to 8.sp
        LogoSize.Medium -> 28.sp to 11.sp
        LogoSize.Large -> 36.sp to 14.sp
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "IF",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            ),
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Bank",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = subtitleSize,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            ),
            color = color.copy(alpha = 0.8f)
        )
    }
}

enum class LogoSize { Small, Medium, Large }
