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
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TotalTextField(
    total: String,
    onTotalChanged: (String) -> Unit,
    error: String?,
    modifier: Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = total,
        onValueChange = onTotalChanged,
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

@Preview
@Composable
fun TotalTextFieldPreview() {
    TotalTextField(
        total = "500.0",
        onTotalChanged = {},
        error = null,
        modifier = Modifier
    )
}

@Preview
@Composable
fun TotalTextFieldPreviewEmpty() {
    TotalTextField(
        total = "  ",
        onTotalChanged = {},
        error = "Total is required",
        modifier = Modifier
    )
}

@Preview
@Composable
fun TotalTextFieldPreviewZero() {
    TotalTextField(
        total = "0",
        onTotalChanged = {},
        error = "Total must be greater than zero",
        modifier = Modifier
    )
}

@Preview
@Composable
fun TotalTextFieldPreviewInvalid() {
    TotalTextField(
        total = "ABC78.0",
        onTotalChanged = {},
        error = "Invalid total amount",
        modifier = Modifier
    )
}