package com.inegru.expensetrackr.data.repository

import com.inegru.expensetrackr.data.db.dao.ExpenseDao
import com.inegru.expensetrackr.data.db.model.asExternalModel
import com.inegru.expensetrackr.data.util.asEntity
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override val allExpenses: Flow<List<Expense>> =
        expenseDao.getAllExpenses().map { it.map { entity -> entity.asExternalModel() } }

    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense.asEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.asEntity())
    }

    override suspend fun getExpenseById(expenseId: Int): Expense? {
        return expenseDao.getExpenseById(expenseId)?.asExternalModel()
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.asEntity())
    }
}