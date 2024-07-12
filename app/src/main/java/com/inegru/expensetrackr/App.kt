package com.inegru.expensetrackr

import android.app.Application
import com.inegru.expensetrackr.common.di.dispatcherProviderModule
import com.inegru.expensetrackr.data.di.dataModule
import com.inegru.expensetrackr.ui.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initInjection()
    }

    private fun initInjection() {
        // Start Koin, collect all Koin-modules from all over the project
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    appModule,
                    dataModule,
                    dispatcherProviderModule,
                    dataModule,
                )
            )
        }
    }
}