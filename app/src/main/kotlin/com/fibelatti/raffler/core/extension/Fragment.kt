package com.fibelatti.raffler.core.extension

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.setTitle(@StringRes title: Int) {
    setTitle(getString(title))
}

fun Fragment.setTitle(title: String) {
    activity?.title = title
}
