package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TestCoroutineLauncher @Inject constructor() : CoroutineLauncher, CoroutineScope {
    private val parentJob by lazy { Job() }
    private val main get() = Dispatchers.Unconfined
    private val background get() = Dispatchers.Unconfined

    override val coroutineContext: CoroutineContext get() = main + parentJob

    override fun cancelAllJobs() {
        parentJob.cancel()
    }

    override fun start(block: suspend CoroutineScope.() -> Unit): Job = launch { runBlocking { block() } }

    override fun startInBackground(
        block: suspend CoroutineScope.() -> Unit
    ): Job = launch(background) { runBlocking { block() } }

    override suspend fun <T> callInBackground(
        block: suspend CoroutineScope.() -> T
    ): T = runBlocking { block() }

    override suspend fun <T> CoroutineScope.defer(
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = async(background) { runBlocking { block() } }
}
