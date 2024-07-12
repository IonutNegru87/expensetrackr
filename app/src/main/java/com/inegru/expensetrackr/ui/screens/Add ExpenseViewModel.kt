package com.inegru.expensetrackr.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.common.utils.DateUtils
import com.inegru.expensetrackr.data.converter.asEntity
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _total = MutableStateFlow("")
    val total: StateFlow<String> = _total.asStateFlow()

    private val _currency = MutableStateFlow("")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _totalError = MutableStateFlow<String?>(null)
    val totalError: StateFlow<String?> = _totalError.asStateFlow()

    private val _currencyError = MutableStateFlow<String?>(null)
    val currencyError: StateFlow<String?> = _currencyError.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updatePhotoUri(uri: Uri?) {
        _photoUri.value = uri
        validateForm()
    }

    fun updateDate(newDate: String) {
        _date.value = newDate
        validateForm()
    }

    fun updateTotal(newTotal: String) {
        _total.value = newTotal
        validateTotal(newTotal)
        validateForm()
    }

    fun updateCurrency(newCurrency: String) {
        _currency.value = newCurrency
        validateCurrency(newCurrency)
        validateForm()
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
    }

    private fun validateTotal(total: String) {
        _totalError.value = when {
            total.isBlank() -> "Total is required"
            total.toDouble() <= 0 -> "Total must be greater than zero"
            total.toDoubleOrNull() == null -> "Invalid total amount"
            else -> null
        }
    }

    private fun validateCurrency(currency: String) {
        _currencyError.value = when {
            currency.isBlank() -> "Currency is required"
            currency.length != 3 -> "Currency must be a 3-letter code"
            else -> null
        }
    }

    private fun validateForm() {
        _isFormValid.value = _photoUri.value != null &&
                _date.value.isNotBlank() &&
                _total.value.isNotBlank() &&
                _currency.value.isNotBlank() &&
                _totalError.value == null &&
                _currencyError.value == null
    }

    fun saveExpense() {
        val expense = Expense(
            id = 0,
            photoUri = _photoUri.value.toString(),
            date = DateUtils.parseDate(_date.value) ?: LocalDate.now(),
            total = _total.value.toDoubleOrNull() ?: 0.0,
            currency = _currency.value,
            description = _description.value
        )

        viewModelScope.launch(dispatcherProvider.io) {
            try {
                expenseRepository.insertExpense(expense.asEntity())
                _uiEvent.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.SaveError(e.message ?: "Unknown error occurred"))
            }
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
        data class SaveError(val message: String) : UiEvent()
    }
}