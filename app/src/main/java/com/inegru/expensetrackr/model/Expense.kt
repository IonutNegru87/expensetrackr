package com.inegru.expensetrackr.model

import java.util.Date

data class Expense(
    val id: Int = 0,
    val photoUri: String,
    val date: Date,
    val total: Double,
    val currency: String,
    val description: String? = null
)