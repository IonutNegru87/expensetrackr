package com.inegru.expensetrackr.ui.di

import com.inegru.expensetrackr.ui.screens.addexpense.AddExpenseViewModel
import com.inegru.expensetrackr.ui.screens.expensedetails.ExpenseDetailsViewModel
import com.inegru.expensetrackr.ui.screens.expenselist.ExpenseListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AddExpenseViewModel(
            expenseRepository = get(),
            dispatcherProvider = get()
        )
    }
    viewModel {
        ExpenseListViewModel(
            expenseRepository = get(),
            dispatcherProvider = get()
        )
    }
    viewModel {
        ExpenseDetailsViewModel(
            repository = get(),
            dispatcherProvider = get()
        )
    }
}
