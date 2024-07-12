package com.inegru.expensetrackr.ui.screens.expenselist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(expenses) { expense ->
                ExpenseListItem(expense = expense) {
                    navController.navigate("expenseDetails/${it.id}")
                }
            }
        }
    }
}

@Composable
fun ExpenseListItem(
    expense: Expense,
    onItemClick: (Expense) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(expense) }
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

    ExpenseListItem(expense = sampleExpense) {}
}