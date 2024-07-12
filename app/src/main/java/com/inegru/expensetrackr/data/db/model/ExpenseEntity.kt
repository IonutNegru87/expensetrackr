package com.inegru.expensetrackr.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.inegru.expensetrackr.model.Expense
import java.time.LocalDate

@Entity(
    tableName = "expenses"
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("photo_uri")
    val photoUri: String,
    val date: LocalDate,
    val total: Double,
    val currency: String,
    val description: String? = null
)

fun ExpenseEntity.asExternalModel() = Expense(
    id = this.id,
    photoUri = this.photoUri,
    date = this.date,
    total = this.total,
    currency = this.currency,
    description = this.description
)