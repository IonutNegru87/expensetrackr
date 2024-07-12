package com.inegru.expensetrackr.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.ext.createImageFile
import com.inegru.expensetrackr.ui.components.CurrencyPickerField
import com.inegru.expensetrackr.ui.components.DatePickerField
import com.inegru.expensetrackr.ui.components.ExpensePhoto
import com.inegru.expensetrackr.ui.components.TotalTextField
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    viewModel: AddExpenseViewModel = koinViewModel(),
    dispatcherProvider: DispatcherProvider = koinInject()
) {
    val photoUri by viewModel.photoUri.collectAsState()
    val date by viewModel.date.collectAsState()
    val total by viewModel.total.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val description by viewModel.description.collectAsState()
    val totalError by viewModel.totalError.collectAsState()
    val currencyError by viewModel.currencyError.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", file
    )
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    viewModel.updatePhotoUri(uri)
                }
            })

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope { dispatcherProvider.ui }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddExpenseViewModel.UiEvent.SaveSuccess -> navController.navigateUp()
                is AddExpenseViewModel.UiEvent.SaveError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = "OK",
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Expense") }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo/Expense
            ExpensePhoto(
                uri = photoUri,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Button(onClick = { launcher.launch(uri) }) {
                Text("Take Photo")
            }

            // Date
            Spacer(modifier = Modifier.height(8.dp))
            DatePickerField(
                date = date,
                onDateChange = viewModel::updateDate,
                modifier = Modifier.fillMaxWidth()
            )

            // Total
            Spacer(modifier = Modifier.height(8.dp))
            TotalTextField(
                total = total,
                onTotalChanged = viewModel::updateTotal,
                error = totalError,
                modifier = Modifier.fillMaxWidth()
            )

            // Currency
            Spacer(modifier = Modifier.height(8.dp))
            CurrencyPickerField(
                currency = currency,
                onCurrencyChange = viewModel::updateCurrency,
                error = currencyError,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Save expense
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::saveExpense,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Expense")
            }
        }
    }
}