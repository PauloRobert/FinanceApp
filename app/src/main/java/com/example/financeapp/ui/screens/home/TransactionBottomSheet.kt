package com.example.financeapp.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import com.example.financeapp.ui.components.DatePickerDialogComponent
import com.example.financeapp.ui.components.DateTimeField
import com.example.financeapp.ui.components.TimePickerDialogComponent
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
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                if (transaction == null) "Nova transação" else "Editar transação",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

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

            //descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError,

                )
            Spacer(modifier = Modifier.height(8.dp))
            //Valor
            OutlinedTextField(
                value = amountField,
                onValueChange = { newValue ->

                    val digits = newValue.text.replace("\\D".toRegex(), "")
                    amount = digits
                    val formatted = formatCurrency(digits)

                    amountField = TextFieldValue(
                        text = formatted,
                        selection = androidx.compose.ui.text.TextRange(formatted.length)
                    )
                    amountError = false
                },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError
            )
            Spacer(modifier = Modifier.height(8.dp))
            //tipo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = isIncome,
                    onClick = { isIncome = true },
                    label = { Text("Entrada") }
                )
                FilterChip(
                    selected = !isIncome,
                    onClick = { isIncome = false },
                    label = { Text("Saída") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (descriptionError || amountError) {
                Text("Preencha todos os campos", color = Color.Red)
            }

            Button(onClick = {
                //123456.0
                val value = amount.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal.ZERO
                descriptionError = description.isBlank()
                amountError = value <= BigDecimal.ZERO

                if (!descriptionError && !amountError) {
                    Log.d("SalvarBanco", dateTime.toString())
                    Log.d("SalvarBanco", description)
                    Log.d("SalvarBanco", value.toString())
                    Log.d("SalvarBanco", isIncome.toString())
                    onSave(description, value, dateTime, isIncome)
                }

            })
            { Text("Salvar") }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}