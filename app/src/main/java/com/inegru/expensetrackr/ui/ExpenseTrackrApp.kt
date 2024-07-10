package com.inegru.expensetrackr.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inegru.expensetrackr.ui.screens.AddExpenseScreen
import com.inegru.expensetrackr.ui.screens.ExpenseListScreen

@Composable
fun ExpenseTrackrApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "expenseList") {
        composable("expenseList") {
            ExpenseListScreen(navController)
        }
        composable("addExpense") {
            AddExpenseScreen(navController)
        }
    }
}

