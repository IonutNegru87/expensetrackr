package com.inegru.expensetrackr.model

import java.time.LocalDate

data class Expense(
    val id: Int = 0,
    val photoUri: String,
    val date: LocalDate,
    val total: Double,
    val currency: String,
    val description: String? = null
)