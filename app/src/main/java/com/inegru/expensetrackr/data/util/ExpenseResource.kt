package com.inegru.expensetrackr.data.util

import com.inegru.expensetrackr.data.db.model.ExpenseEntity
import com.inegru.expensetrackr.model.Expense

internal fun Expense.asEntity() = ExpenseEntity(
    id = this.id,
    photoUri = this.photoUri,
    date = this.date,
    total = this.total,
    currency = this.currency,
    description = this.description
)