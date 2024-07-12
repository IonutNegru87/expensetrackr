package com.inegru.expensetrackr.ui.screens.expensedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenseDetailsViewModel(
    private val repository: ExpenseRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense

    fun getExpenseById(expenseId: Int) {
        viewModelScope.launch(dispatcherProvider.io) {
            _expense.value = repository.getExpenseById(expenseId)
        }
    }
}