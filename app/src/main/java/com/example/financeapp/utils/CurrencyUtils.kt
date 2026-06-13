package com.example.financeapp.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: String): String {
    if (value.isEmpty()) return ""

    val number = value.toLongOrNull() ?: 0L
    val formatted = number / 100.0
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

    return format.format(formatted)
}

fun formatCurrencyBr(value: BigDecimal): String {
    val format = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("pt-BR")
    )
    return format.format(value)
}