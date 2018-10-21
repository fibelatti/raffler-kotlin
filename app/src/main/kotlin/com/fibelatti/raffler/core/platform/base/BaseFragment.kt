package com.fibelatti.raffler.core.platform.base

import androidx.fragment.app.Fragment
import com.fibelatti.raffler.core.platform.DismissibleHint
import com.fibelatti.raffler.core.platform.DismissibleHintDelegate

abstract class BaseFragment :
    Fragment(),
    DismissibleHint by DismissibleHintDelegate() {

    protected val injector by lazy { (activity as BaseActivity).injector }
    protected val viewModelFactory by lazy { (activity as BaseActivity).viewModelFactory }

    open fun handleError(error: Throwable) {
        error.printStackTrace()
    }
}
