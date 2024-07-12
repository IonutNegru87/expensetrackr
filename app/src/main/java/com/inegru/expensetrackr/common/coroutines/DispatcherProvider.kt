package com.inegru.expensetrackr.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * The DispatcherProvider provides [CoroutineDispatcher] for usage in the app.
 */
interface DispatcherProvider {

    /**
     * Provides a [CoroutineDispatcher] used for UI operations.
     */
    val ui: CoroutineDispatcher

    /**
     * Provides a [CoroutineDispatcher] used for IO operations.
     */
    val io: CoroutineDispatcher
}