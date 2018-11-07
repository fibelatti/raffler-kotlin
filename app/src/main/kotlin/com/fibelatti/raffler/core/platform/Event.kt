package com.fibelatti.raffler.core.platform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Wrapper class for data that is posted to [MutableLiveData] but should be observed only once.
 * This is specially useful when the lifecycle owner of a ViewModel is an Activity.
 *
 * @param <T> The type of data hold by this instance
 */
data class Event<out T>(private val content: T?) {

    private var alreadyObserved = false

    /**
     * @return the [content] if this [Event] was not [alreadyObserved], null otherwise
     */
    fun getContent(): T? = if (alreadyObserved) null else content.also { alreadyObserved = true }

    /**
     * @return the [content], even if [alreadyObserved] is true. Calling this has no change
     * [alreadyObserved]'s value.
     */
    fun peekContent(): T? = content
}

/**
 * Alias to simplify declarations of [MutableLiveData] with type [Event] of <T>.
 *
 * @param <T> The type of data hold by this instance
 */
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Wraps [value] in an [Event] instance before calling [MutableLiveData.setValue]
 *
 * @param <T> The type of data hold by this instance
 */
fun <T> MutableLiveEvent<T>.setEvent(value: T?) = setValue(Event(value))

/**
 * Wraps [value] in an [Event] instance before calling [MutableLiveData.postValue]
 *
 * @param <T> The type of data hold by this instance
 */
fun <T> MutableLiveEvent<T>.postEvent(value: T?) = postValue(Event(value))

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been observed.
 *
 * [onEvent] is *only* called if the [Event.alreadyObserved] is false.
 *
 * @param <T> The type of data hold by this instance
 */
class EventObserver<T>(private val onEvent: (T) -> Unit) : Observer<Event<T>?> {
    override fun onChanged(event: Event<T>?) {
        event?.getContent()?.let(onEvent)
    }
}
