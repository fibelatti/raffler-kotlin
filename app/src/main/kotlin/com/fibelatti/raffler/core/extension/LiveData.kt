package com.fibelatti.raffler.core.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fibelatti.core.extension.safeLet

fun <A: Any, B: Any> LiveData<A>.combineLatest(b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        addSource(this@combineLatest) {
            if (it == null && value != null) {
                value = null
            }
            lastA = it
            safeLet(lastA, lastB) { a: A, b: B -> value = a to b }
        }

        addSource(b) {
            if (it == null && value != null) {
                value = null
            }
            lastB = it
            safeLet(lastA, lastB) { a: A, b: B -> value = a to b }
        }
    }
}
