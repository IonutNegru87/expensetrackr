package com.inegru.expensetrackr.data.db.di

import com.inegru.expensetrackr.data.db.ExpenseDatabase
import org.koin.dsl.module

val daosModule = module {
    includes(dbModule)

    single { get<ExpenseDatabase>().expenseDao() }
}