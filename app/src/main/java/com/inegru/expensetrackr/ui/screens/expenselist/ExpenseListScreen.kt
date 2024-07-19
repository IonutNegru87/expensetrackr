package com.inegru.expensetrackr.ui.screens.expenselist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.inegru.expensetrackr.model.Expense
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    navController: NavHostController,
    viewModel: ExpenseListViewModel = koinViewModel(),
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    var confirmationExpenseDelete by rememberSaveable { mutableStateOf<Expense?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addExpense") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { innerPadding ->
        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items saved.\n\n" +
                            "Use FAB (+) to start adding new expenses!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(expenses) { expense ->
                ExpenseListItem(
                    expense = expense,
                    onItemClick = {
                        navController.navigate("expenseDetails/${it.id}")
                    },
                    onItemLongClick = {
                        confirmationExpenseDelete = expense
                    }
                )
            }
        }
    }

    confirmationExpenseDelete?.let {
        val expenseId = it.id
        val openDialog = remember { mutableStateOf(true) }
        when {
            openDialog.value -> {
                BasicAlertDialog(
                    onDismissRequest = {
                        confirmationExpenseDelete = null
                        openDialog.value = false
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Do you want to delete the Expense with the ID: $expenseId ?",
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            TextButton(
                                onClick = {
                                    confirmationExpenseDelete = null
                                    openDialog.value = false
                                    viewModel.deleteExpense(it)
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(text = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseListItem(
    expense: Expense,
    onItemClick: (Expense) -> Unit = {},
    onItemLongClick: (Expense) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onItemClick(expense) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onItemLongClick(expense)
                },
                onLongClickLabel = "Delete Expense"
            )
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "ID: ${expense.id}")
            Text(text = "Date: ${expense.date}")
            Text(text = "Total: ${expense.total} ${expense.currency}")
            Text(text = "Description: ${expense.description ?: "N/A"}")
        }
    }
}

@Preview
@Composable
fun ExpenseListItemPreview() {
    val sampleExpense = Expense(
        id = 1,
        photoUri = "sample_uri",
        date = LocalDate.now(),
        total = 100.0,
        currency = "USD",
        description = "Sample expense description"
    )

    ExpenseListItem(expense = sampleExpense)
}