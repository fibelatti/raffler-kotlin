package com.fibelatti.raffler.core.platform

import androidx.fragment.app.Fragment

abstract class BaseFragment :
    Fragment(),
    HintDisplayer by HintDisplayerDelegate() {

    protected val injector by lazy { (activity as BaseActivity).injector }
    protected val viewModelFactory by lazy { (activity as BaseActivity).viewModelFactory }

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }
}
