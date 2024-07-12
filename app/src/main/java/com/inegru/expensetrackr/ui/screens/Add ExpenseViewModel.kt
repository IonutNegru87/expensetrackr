package com.inegru.expensetrackr.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.converter.asEntity
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.launch
import java.util.Date

class AddExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    fun saveExpense() {
        val expense = Expense(
            id = 0,
            photoUri = "PHOTO_URL",
            date = Date(),
            total = 500.0,
            currency = "EUR",
            description = "DESCRIPTION_TEST"
        )

        viewModelScope.launch(dispatcherProvider.io) {
            try {
                expenseRepository.insertExpense(expense.asEntity())
                // Emit a success event here
            } catch (e: Exception) {
                //Emit an error event here
            }
        }
    }

}