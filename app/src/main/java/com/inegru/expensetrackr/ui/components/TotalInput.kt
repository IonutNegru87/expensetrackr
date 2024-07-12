package com.inegru.expensetrackr.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TotalTextField(
    total: String,
    onTotalChanged: (String) -> Unit,
    error: String?,
    onValidationError: (String?) -> Unit,
    modifier: Modifier,
) {

    fun validateTotal(total: String): String? {
        return when {
            total.isEmpty() -> "Total is required"
            total.toDoubleOrNull() == null -> "Invalid number format"
            total.toDouble() <= 0 -> "Total must be greater than zero"
            else -> null
        }
    }

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = total,
        onValueChange = {
            onTotalChanged(it)
            onValidationError(validateTotal(it))
        },
        label = { Text("Total") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
        isError = error != null,
        supportingText = {
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        },
        singleLine = true,
        modifier = modifier,
    )
}