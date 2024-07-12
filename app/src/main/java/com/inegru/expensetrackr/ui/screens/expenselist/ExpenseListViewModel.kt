package com.inegru.expensetrackr.ui.screens.expenselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val expenseRepository: ExpenseRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses = _expenses

    init {
        // Fetch expenses initially
        fetchExpenses()
    }

    private fun fetchExpenses() {
        viewModelScope.launch(dispatcherProvider.io) {
            expenseRepository.allExpenses.collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

}
