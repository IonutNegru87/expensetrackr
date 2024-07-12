package com.inegru.expensetrackr.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photoUri: String,
    val date: LocalDate,
    val total: Double,
    val currency: String,
    val description: String? = null
)