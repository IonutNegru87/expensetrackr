package com.inegru.expensetrackr.data.repository

import com.inegru.expensetrackr.db.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    val allExpenses: Flow<List<ExpenseEntity>>

    suspend fun insertExpense(expenseEntity: ExpenseEntity)

    suspend fun deleteExpense(expenseEntity: ExpenseEntity)

}