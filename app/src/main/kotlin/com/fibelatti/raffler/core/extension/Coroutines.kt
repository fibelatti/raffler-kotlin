package com.fibelatti.raffler.core.extension

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

fun launchAsync(asyncJobs: ArrayList<Job>? = null, block: suspend CoroutineScope.() -> Unit) {
    launch(UI) { block() }
        .also { job ->
            asyncJobs?.add(job)
            job.invokeOnCompletion { asyncJobs?.remove(job) }
        }
}

suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> = async(CommonPool) { block() }

suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T = async(block).await()
