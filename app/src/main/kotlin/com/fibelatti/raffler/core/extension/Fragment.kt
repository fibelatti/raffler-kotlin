package com.fibelatti.raffler.core.extension

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fibelatti.raffler.R

inline fun <reified T : ViewModel> Fragment.viewModel(
    factory: ViewModelProvider.Factory,
    noinline body: (T.() -> Unit)? = null
): T {
    val vm = ViewModelProviders.of(this, factory)[T::class.java]
    body?.let { vm.it() }
    return vm
}

fun Fragment.setTitle(@StringRes title: Int) {
    setTitle(getString(title))
}

fun Fragment.setTitle(title: String) {
    activity?.title = title
}

inline fun Fragment.inTransaction(
    allowStateLoss: Boolean = false,
    block: FragmentTransaction.() -> FragmentTransaction
) {
    fragmentManager?.inTransaction(allowStateLoss) { block() }
}

inline fun FragmentManager.inTransaction(
    allowStateLoss: Boolean = false,
    block: FragmentTransaction.() -> FragmentTransaction
) {
    beginTransaction().block().run {
        return@run if (allowStateLoss) commitAllowingStateLoss() else commit()
    }
}

fun FragmentTransaction.withDefaultAnimations(): FragmentTransaction = apply {
    setCustomAnimations(
        R.anim.slide_right_in,
        R.anim.slide_left_out,
        R.anim.slide_left_in,
        R.anim.slide_right_out
    )
}
