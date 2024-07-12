package com.inegru.expensetrackr.data.di

import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.data.repository.ExpenseRepositoryImpl
import com.inegru.expensetrackr.db.di.daosModule
import org.koin.dsl.module

val dataModule = module {
    includes(daosModule)

    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
}