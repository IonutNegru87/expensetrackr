package com.inegru.expensetrackr.common.di

import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.common.coroutines.DispatcherProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val dispatcherProviderModule: Module = module {
    single<DispatcherProvider> { DispatcherProviderImpl() }
}