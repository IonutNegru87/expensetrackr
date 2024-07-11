package com.inegru.expensetrackr.data.repository

import com.inegru.expensetrackr.db.dao.ExpenseDao
import com.inegru.expensetrackr.db.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {

    override val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    override suspend fun insertExpense(expenseEntity: ExpenseEntity) {
        expenseDao.insertExpense(expenseEntity)
    }

    override suspend fun deleteExpense(expenseEntity: ExpenseEntity) {
        expenseDao.deleteExpense(expenseEntity)
    }
}