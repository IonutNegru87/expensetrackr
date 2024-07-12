package com.inegru.expensetrackr.db.di

import com.inegru.expensetrackr.db.ExpenseDatabase
import org.koin.dsl.module

val daosModule = module {
    includes(dbModule)

    single { get<ExpenseDatabase>().expenseDao() }
}