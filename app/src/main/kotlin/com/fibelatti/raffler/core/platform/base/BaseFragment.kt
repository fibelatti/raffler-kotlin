package com.fibelatti.raffler.core.platform.base

import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.fibelatti.core.extension.toast
import com.fibelatti.raffler.BuildConfig
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.di.ViewModelProvider
import com.fibelatti.raffler.core.platform.DismissibleHint
import com.fibelatti.raffler.core.platform.DismissibleHintDelegate

abstract class BaseFragment @ContentView constructor(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId),
    DismissibleHint by DismissibleHintDelegate() {

    protected val viewModelProvider: ViewModelProvider
        get() = (activity as BaseActivity).viewModelProvider

    open fun handleError(error: Throwable) {
        activity?.toast(getString(R.string.generic_msg_error))
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
        }
    }
}
