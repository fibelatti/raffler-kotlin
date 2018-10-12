package com.fibelatti.raffler.core.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.fibelatti.raffler.core.platform.EventObserver
import com.fibelatti.raffler.core.platform.Event

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, Observer { it?.let(body) })

fun <T : Any, L : LiveData<Event<T>>> LifecycleOwner.observeEvent(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, EventObserver(body))

fun <L : LiveData<Throwable>> LifecycleOwner.error(liveData: L, body: (Throwable) -> Unit) =
    liveData.observe(this, Observer { it?.let(body) })
