package com.inegru.expensetrackr.ui.screens.expenselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val expenseRepository: ExpenseRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses = _expenses

    init {
        fetchExpenses()
    }

    private fun fetchExpenses() {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                expenseRepository.allExpenses.collect { expenses ->
                    _expenses.value =
                        expenses.sortedWith(compareByDescending<Expense> { it.date }.thenByDescending { it.id })
                    _errorState.value = null
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "An unknown error occurred"
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

}
