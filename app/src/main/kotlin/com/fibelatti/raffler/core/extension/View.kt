package com.fibelatti.raffler.core.extension

import android.animation.LayoutTransition
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

@JvmOverloads
fun ViewGroup.inflate(@LayoutRes layoutResource: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutResource, this, attachToRoot)

fun ViewGroup.setShapeBackgroundColor(color: Int) {
    val background = background
    when (background) {
        is ShapeDrawable -> background.paint?.color = color
        is GradientDrawable -> background.setColor(color)
        is ColorDrawable -> background.color = color
    }
}

fun ViewGroup.animateChangingTransitions() {
    layoutTransition = LayoutTransition().apply { enableTransitionType(LayoutTransition.CHANGING) }
}

@JvmOverloads
fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

// region Components
fun TextInputLayout.showError(errorMessage: String) {
    error = errorMessage
    showKeyboard()
}

fun TextInputLayout.clearError() {
    error = null
}

fun EditText.textAsString(): String = this.text.toString()

fun EditText.clearText() {
    setText("")
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            editable?.let { afterTextChanged(it.toString()) }
        }
    })
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visibleIf(predicate: Boolean, otherwiseVisibility: Int = View.GONE) {
    visibility = if (predicate) View.VISIBLE else otherwiseVisibility
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.heightCollapse() {
    val params = layoutParams
    params.height = 0
    layoutParams = params
    requestLayout()
}

fun View.heightWrapContent() {
    val params = layoutParams
    params.height = when (layoutParams) {
        is LinearLayout -> LinearLayout.LayoutParams.WRAP_CONTENT
        is RelativeLayout -> RelativeLayout.LayoutParams.WRAP_CONTENT
        is FrameLayout -> FrameLayout.LayoutParams.WRAP_CONTENT
        else -> ViewGroup.LayoutParams.WRAP_CONTENT
    }

    layoutParams = params
    requestLayout()
}

fun RecyclerView.withDefaultDecoration(): RecyclerView = apply {
    addItemDecoration(ItemOffsetDecoration(context, R.dimen.margin_small))
}

fun RecyclerView.withGridLayoutManager(spanCount: Int): RecyclerView = apply {
    layoutManager = GridLayoutManager(context, spanCount)
}

fun RecyclerView.withLinearLayoutManager(): RecyclerView = apply {
    layoutManager = LinearLayoutManager(context)
}
// endregion

// region Keyboard
fun isKeyboardSubmit(actionId: Int, event: KeyEvent?): Boolean =
    actionId == EditorInfo.IME_ACTION_GO ||
        actionId == EditorInfo.IME_ACTION_DONE ||
        (event != null && event.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_ENTER)

fun View.showKeyboard() {
    requestFocus()
    context.getSystemService<InputMethodManager>()?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    context.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(windowToken, 0)
}
// endregion
