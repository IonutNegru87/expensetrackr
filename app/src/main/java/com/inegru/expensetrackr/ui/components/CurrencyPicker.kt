package com.inegru.expensetrackr.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.PopupProperties

@Composable
fun CurrencyPickerField(
    currency: String,
    onCurrencyChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var isCustomInput by remember { mutableStateOf(currency.isEmpty()) }
    val predefinedCurrencies = listOf("USD", "EUR", "RON")

    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = currency,
            onValueChange = {
                onCurrencyChange(it)
            },
            label = { Text("Currency") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!isCustomInput) expanded = true },
            readOnly = !isCustomInput,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select currency")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = false)
        ) {
            predefinedCurrencies.forEach { currencyOption ->
                DropdownMenuItem(
                    text = { Text(currencyOption) },
                    onClick = {
                        onCurrencyChange(currencyOption)
                        expanded = false
                        isCustomInput = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Custom") },
                onClick = {
                    isCustomInput = true
                    expanded = false
                    onCurrencyChange("")
                }
            )
        }
    }
}