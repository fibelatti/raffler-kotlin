package com.fibelatti.raffler.core.platform

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import com.fibelatti.raffler.core.provider.ThreadProvider
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseViewModel(
    private val threadProvider: ThreadProvider
) : ViewModel(), CoroutineScope {

    private val parentJob by lazy { Job() }

    val error by lazy { MutableLiveData<Throwable>() }

    override val coroutineContext: CoroutineContext
        get() = threadProvider.main() + parentJob

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    protected fun handleError(error: Throwable) {
        this.error.value = error
    }

    protected suspend fun <T> CoroutineScope.inBackground(
        block: suspend CoroutineScope.() -> T
    ): T = async(threadProvider.background()) { block() }.await()
}
