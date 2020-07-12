package com.fibelatti.raffler.core.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T> Fragment.getNavigationResult(key: String = "result"): T? =
    findNavController().currentBackStackEntry?.savedStateHandle?.get(key)

inline fun <T : Any> Fragment.observeNavigationResult(
    key: String = "result",
    crossinline body: (T) -> Unit
) {
   findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
       ?.observe(viewLifecycleOwner, Observer { it?.let(body) })
}
