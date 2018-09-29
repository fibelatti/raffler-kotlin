package com.fibelatti.raffler.core.platform

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setTitle

abstract class BaseFragment :
    Fragment(),
    HintDisplayer by HintDisplayerDelegate() {

    protected val injector by lazy { (activity as BaseActivity).injector }
    protected val viewModelFactory by lazy { (activity as BaseActivity).viewModelFactory }

    @CallSuper
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setTitle(R.string.app_name)
    }

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }

    fun close() = fragmentManager?.popBackStack()
}
