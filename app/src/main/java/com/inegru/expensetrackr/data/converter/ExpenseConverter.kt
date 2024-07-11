package com.inegru.expensetrackr.data.converter

import com.inegru.expensetrackr.db.model.ExpenseEntity
import com.inegru.expensetrackr.model.Expense

internal fun ExpenseEntity.asModel() = Expense(
    id = this.id,
    photoUri = this.photoUri,
    date = this.date,
    total = this.total,
    currency = this.currency,
    description = this.description
)

internal fun Expense.asEntity() = ExpenseEntity(
    id = this.id,
    photoUri = this.photoUri,
    date = this.date,
    total = this.total,
    currency = this.currency,
    description = this.description
)