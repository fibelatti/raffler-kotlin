package com.fibelatti.raffler.core.platform

import android.support.v4.app.Fragment

abstract class BaseFragment :
    Fragment(),
    HintDisplayer by HintDisplayerDelegate() {

    protected val injector get() = (activity as? BaseActivity)?.injector

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }

    fun close() = fragmentManager?.popBackStack()
}
