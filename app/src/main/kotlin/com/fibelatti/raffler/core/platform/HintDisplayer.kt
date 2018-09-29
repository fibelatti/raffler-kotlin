package com.fibelatti.raffler.core.platform

import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.inflate
import com.fibelatti.raffler.core.extension.setShapeBackgroundColor

interface HintDisplayer {
    fun showDismissibleHint(container: FrameLayout, hintTitle: String, hintMessage: String)
}

class HintDisplayerDelegate : HintDisplayer {
    override fun showDismissibleHint(container: FrameLayout, hintTitle: String, hintMessage: String) {
        container.inflate(R.layout.layout_dismissible_hint)
            .apply {
                val rootLayout = findViewById<LinearLayout>(R.id.layoutRoot)
                val textViewHintTitle = findViewById<AppCompatTextView>(R.id.textViewHintTitle)
                val textViewHintMessage = findViewById<AppCompatTextView>(R.id.textViewHintMessage)
                val buttonHintDismiss = findViewById<AppCompatTextView>(R.id.textViewButtonDismiss)

                rootLayout.setShapeBackgroundColor(ContextCompat.getColor(rootLayout.context, R.color.color_background_contrast))

                textViewHintTitle.text = hintTitle
                textViewHintMessage.text = hintMessage

                buttonHintDismiss.setOnClickListener { container.removeView(this) }
            }
            .let { container.addView(it) }
    }
}
