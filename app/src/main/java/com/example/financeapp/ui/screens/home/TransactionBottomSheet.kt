package com.example.financeapp.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.ui.components.DatePickerDialogComponent
import com.example.financeapp.ui.components.DateTimeField
import com.example.financeapp.ui.components.GradientButton
import com.example.financeapp.ui.components.TimePickerDialogComponent
import com.example.financeapp.ui.theme.IFExpense
import com.example.financeapp.ui.theme.IFIncome
import com.example.financeapp.utils.formatCurrency
import com.example.financeapp.utils.formatCurrencyBr
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    transaction: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (String, BigDecimal, LocalDateTime, Boolean) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountField by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(true) }

    var descriptionError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    var dateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(transaction) {
        transaction?.let {
            description = it.description
            val digits = (it.amount)
            val formatted = formatCurrencyBr(digits)
            amountField =
                (TextFieldValue(
                    text = formatted,
                    selection = TextRange(formatted.length)
                ))
            dateTime = it.date
            amount = it.amount.multiply(BigDecimal(100)).toBigInteger().toString()
            isIncome = it.type == TransactionType.INCOME
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                if (transaction == null) "Nova transação" else "Editar transação",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(20.dp))

            DateTimeField(
                dateTime = dateTime.format(formatter),
                onClick = {
                    showDatePicker = true
                }
            )

            if (showDatePicker) {
                DatePickerDialogComponent(
                    onDateSelected = { selected ->
                        dateTime = selected
                    },
                    onDismiss = {
                        showDatePicker = false
                        showTimePicker = true
                    }
                )
            }

            if (showTimePicker) {
                TimePickerDialogComponent(
                    onTimeSelected = { hour, minute ->
                        dateTime = dateTime.withHour(hour).withMinute(minute)
                    },
                    onDismiss = { showTimePicker = false }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Valor
            OutlinedTextField(
                value = amountField,
                onValueChange = { newValue ->
                    val digits = newValue.text.replace("\\D".toRegex(), "")
                    amount = digits
                    val formatted = formatCurrency(digits)
                    amountField = TextFieldValue(
                        text = formatted,
                        selection = TextRange(formatted.length)
                    )
                    amountError = false
                },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tipo — cards visuais coloridos
            Text(
                "Tipo de transação",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Entrada
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            if (isIncome) IFIncome.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { isIncome = true }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isIncome) IFIncome.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward, null,
                                tint = if (isIncome) IFIncome else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Entrada",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isIncome) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isIncome) IFIncome else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Saída
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            if (!isIncome) IFExpense.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { isIncome = false }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (!isIncome) IFExpense.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowDownward, null,
                                tint = if (!isIncome) IFExpense else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Saída",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (!isIncome) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (!isIncome) IFExpense else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (descriptionError || amountError) {
                Text(
                    "Preencha todos os campos",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            GradientButton(
                text = "Salvar",
                onClick = {
                    val value = amount.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal.ZERO
                    descriptionError = description.isBlank()
                    amountError = value <= BigDecimal.ZERO

                    if (!descriptionError && !amountError) {
                        onSave(description, value, dateTime, isIncome)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}