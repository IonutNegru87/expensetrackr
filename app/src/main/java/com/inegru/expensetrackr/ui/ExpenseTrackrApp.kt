package com.inegru.expensetrackr.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inegru.expensetrackr.ui.screens.addexpense.AddExpenseScreen
import com.inegru.expensetrackr.ui.screens.expensedetails.ExpenseDetailsScreen
import com.inegru.expensetrackr.ui.screens.expenselist.ExpenseListScreen

@Composable
fun ExpenseTrackrApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "expenseList"
    ) {
        composable("expenseList") {
            ExpenseListScreen(navController)
        }
        composable(
            route = "expenseDetails/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getInt("expenseId")
            ExpenseDetailsScreen(
                expenseId = expenseId ?: error("Expense ID not provided"),
                navController = navController
            )
        }
        composable("addExpense") {
            AddExpenseScreen(navController)
        }
    }
}

