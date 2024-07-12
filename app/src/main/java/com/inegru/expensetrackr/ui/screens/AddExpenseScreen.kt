package com.inegru.expensetrackr.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.inegru.expensetrackr.ext.createImageFile
import com.inegru.expensetrackr.ui.components.CurrencyPickerField
import com.inegru.expensetrackr.ui.components.DatePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavHostController) {

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var date by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var totalError by remember { mutableStateOf<String?>(null) }
    var currencyError by remember { mutableStateOf<String?>(null) }

    val file = LocalContext.current.createImageFile()
    val uri = FileProvider.getUriForFile(
        LocalContext.current, "${LocalContext.current.packageName}.fileprovider", file
    )
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    photoUri = uri
                }
            })

    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        TopAppBar(title = { Text("Add Expense") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo/Expense
            photoUri?.let {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUri)
                        .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                        .build()
                )
                if (painter.state is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator()
                } else {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "Expense receipt",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp)
                    )
                }
            }
            Button(onClick = { launcher.launch(uri) }) {
                Text("Take Photo")
            }

            // Date
            Spacer(modifier = Modifier.height(8.dp))
            DatePickerField(
                date = date,
                onDateChange = { date = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Total
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = total,
                onValueChange = {
                    total = it
                    totalError = validateTotal(it)
                },
                label = { Text("Total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
                isError = totalError != null,
                supportingText = {
                    totalError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Currency
            Spacer(modifier = Modifier.height(8.dp))
            CurrencyPickerField(
                currency = currency,
                onCurrencyChange = { currency = it },
                error = currencyError,
                onValidationError = { currencyError = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun validateTotal(total: String): String? {
    return when {
        total.isEmpty() -> "Total is required"
        total.toDoubleOrNull() == null -> "Invalid number format"
        total.toDouble() <= 0 -> "Total must be greater than zero"
        else -> null
    }
}
