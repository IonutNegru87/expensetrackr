package com.inegru.expensetrackr.data.repository

import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    val allExpenses: Flow<List<Expense>>

    suspend fun insertExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    suspend fun getExpenseById(expenseId: Int): Expense?

    suspend fun updateExpense(expense: Expense)
}