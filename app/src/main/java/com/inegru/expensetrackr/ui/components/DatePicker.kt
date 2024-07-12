package com.inegru.expensetrackr.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.inegru.expensetrackr.common.utils.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
            ?.toEpochMilli()
    )

    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selDate =
                        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(selDate)
                }
                onDismiss()
            }, enabled = confirmEnabled.value
        ) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Preview
@Composable
fun DatePickerDialogPreview() {
    DatePickerDialog(
        selectedDate = LocalDate.now(),
        onDateSelected = {},
        onDismiss = {}
    )
}

@Preview
@Composable
fun DatePickerDialogPreviewInvalid() {
    DatePickerDialog(
        selectedDate = null,
        onDateSelected = {},
        onDismiss = {}
    )
}

@Preview
@Composable
fun DatePickerDialogPreviewSpecific() {
    DatePickerDialog(
        selectedDate = LocalDate.of(2024, 7, 31),
        onDateSelected = {},
        onDismiss = {}
    )
}

@Composable
fun DatePickerField(
    date: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(DateUtils.parseDate(date)) }

    OutlinedTextField(
        value = date,
        onValueChange = onDateChange,
        label = { Text("Date") },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Select date")
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        supportingText = {
            Text(text = "", color = MaterialTheme.colorScheme.error)
        },
        readOnly = true,
        enabled = true,
    )

    if (showDialog) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                onDateChange(DateUtils.formatDate(it))
            },
            onDismiss = { showDialog = false })
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerFieldPreview() {
    DatePickerField(
        date = DateUtils.formatDate(LocalDate.now()),
        onDateChange = {},
        modifier = Modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DatePickerFieldPreviewEmpty() {
    DatePickerField(
        date = "",
        onDateChange = {},
        modifier = Modifier
    )
}

//TODO: Will need to fix this
@Preview(showBackground = true)
@Composable
fun DatePickerFieldPreviewInvalid() {
    DatePickerField(
        date = "baba",
        onDateChange = {},
        modifier = Modifier
    )
}