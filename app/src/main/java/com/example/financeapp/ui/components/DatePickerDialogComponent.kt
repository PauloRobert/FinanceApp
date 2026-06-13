package com.example.financeapp.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                state.selectedDateMillis?.let {
                    val selected = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(it),
                        java.time.ZoneId.systemDefault()
                    )
                    onDateSelected(selected)
                }
                onDismiss()
            }) {
                Text("ok")
            }
        }
    ) {
        DatePicker(state = state)
    }
}