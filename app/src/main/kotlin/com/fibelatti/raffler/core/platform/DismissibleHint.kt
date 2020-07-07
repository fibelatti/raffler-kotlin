package com.fibelatti.raffler.core.platform

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.fibelatti.core.extension.inflate
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor

interface DismissibleHint {

    fun showDismissibleHint(
        container: View,
        @StringRes hintTitle: Int,
        @StringRes hintMessage: Int,
        onHintDismissed: (() -> Unit)? = null
    )

    fun showDismissibleHint(
        container: View,
        hintTitle: String,
        hintMessage: String,
        onHintDismissed: (() -> Unit)? = null
    )
}

class DismissibleHintDelegate : DismissibleHint {

    override fun showDismissibleHint(
        container: View,
        hintTitle: Int,
        hintMessage: Int,
        onHintDismissed: (() -> Unit)?
    ) {
        showDismissibleHint(
            container = container,
            hintTitle = container.context.getString(hintTitle),
            hintMessage = container.context.getString(hintMessage),
            onHintDismissed = onHintDismissed
        )
    }

    override fun showDismissibleHint(
        container: View,
        hintTitle: String,
        hintMessage: String,
        onHintDismissed: (() -> Unit)?
    ) {
        (container as? ViewGroup)?.inflate(R.layout.layout_dismissible_hint)
            ?.apply {
                val rootLayout = findViewById<LinearLayout>(R.id.layoutRoot)
                val textViewHintTitle = findViewById<AppCompatTextView>(R.id.textViewHintTitle)
                val textViewHintMessage = findViewById<AppCompatTextView>(R.id.textViewHintMessage)
                val buttonHintDismiss = findViewById<AppCompatTextView>(R.id.textViewButtonDismiss)

                rootLayout.setShapeBackgroundColor(
                    ContextCompat.getColor(rootLayout.context, R.color.color_background_contrast)
                )

                textViewHintTitle.text = hintTitle
                textViewHintMessage.text = hintMessage

                buttonHintDismiss.setOnClickListener {
                    onHintDismissed?.invoke()
                    container.removeView(this)
                }
            }
            ?.let(container::addView)
    }
}
