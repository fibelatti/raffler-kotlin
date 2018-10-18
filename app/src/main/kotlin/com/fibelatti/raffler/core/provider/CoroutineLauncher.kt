package com.fibelatti.raffler.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface CoroutineLauncher {
    fun cancelAllJobs()

    fun start(block: suspend CoroutineScope.() -> Unit): Job

    fun startInBackground(block: suspend CoroutineScope.() -> Unit): Job

    suspend fun <T> callInBackground(block: suspend CoroutineScope.() -> T): T

    suspend fun <T> CoroutineScope.defer(block: suspend CoroutineScope.() -> T): Deferred<T>
}

class CoroutineLauncherDelegate @Inject constructor() : CoroutineLauncher, CoroutineScope {
    private val parentJob by lazy { Job() }
    private val main get() = Dispatchers.Main
    private val background get() = Dispatchers.IO

    override val coroutineContext: CoroutineContext get() = main + parentJob

    override fun cancelAllJobs() {
        parentJob.cancel()
    }

    override fun start(block: suspend CoroutineScope.() -> Unit): Job = launch { block() }

    override fun startInBackground(
        block: suspend CoroutineScope.() -> Unit
    ): Job = launch(background) { block() }

    override suspend fun <T> callInBackground(
        block: suspend CoroutineScope.() -> T
    ): T = withContext(background) { block() }

    override suspend fun <T> CoroutineScope.defer(
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> = async(background) { block() }
}
