package com.inegru.expensetrackr.data.db.di

import com.inegru.expensetrackr.data.db.ExpenseDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

internal val dbModule = module {
    // We can either use single from the DI library or rely on our own singleton implementation
    single { ExpenseDatabase.getDatabase(androidApplication()) }
}