package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TestCoroutineLauncher @Inject constructor() : CoroutineLauncher, CoroutineScope {
    private val parentJob by lazy { Job() }

    @ExperimentalCoroutinesApi
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Unconfined + parentJob

    override fun cancelAllJobs() {
        parentJob.cancel()
    }

    override fun start(block: suspend CoroutineScope.() -> Unit): Job = launch { runBlocking { block() } }

    override fun startInBackground(
        block: suspend CoroutineScope.() -> Unit
    ): Job = launch { runBlocking { block() } }

    override suspend fun <T> callInBackground(
        block: suspend CoroutineScope.() -> T
    ): T = runBlocking { block() }

    override suspend fun <T> CoroutineScope.defer(
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = async { runBlocking { block() } }
}
