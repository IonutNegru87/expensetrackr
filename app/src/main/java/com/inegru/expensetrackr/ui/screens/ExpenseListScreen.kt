package com.inegru.expensetrackr.ui.screens

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(navController: NavHostController) {
    val dummyExpenses = remember {
        listOf("Lunch", "Office Supplies", "Transportation", "Coffee")
    }

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
            items(dummyExpenses) { expense ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = expense,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}