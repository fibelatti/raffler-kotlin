package com.fibelatti.raffler.core.platform.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ActionMode
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.addTextChangedListener
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.orZero
import com.fibelatti.raffler.core.extension.textAsString

private const val XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"
private const val DEFAULT_CHAR_SPACING = 24
private const val DEFAULT_LINE_SPACING = 8
private const val DEFAULT_MAX_LENGTH = 4

class PinEntryEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = R.attr.pinEntryEditTextStyle
) : EditText(context, attrs, defStyleAttr) {

    private val space: Float
    private val lineSpacing: Float
    private val numChars: Int
    private val lineStroke: Float

    private val colorActivated: Int
    private val colorFocused: Int
    private val colorNotFocused: Int
    private val colorError: Int
    private val colorText: Int
    private val textWidths: FloatArray

    var isError: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var onMaxLengthReached: () -> Unit = {}

    init {
        setBackgroundResource(0)

        val typedAttrs = context.obtainStyledAttributes(attrs, R.styleable.PinEntryEditText, defStyleAttr, R.style.AppTheme_PinEntryEditText)
        val multi = context.resources.displayMetrics.density.toInt()

        space = typedAttrs.getDimensionPixelOffset(R.styleable.PinEntryEditText_char_spacing, DEFAULT_CHAR_SPACING * multi).toFloat()
        lineSpacing = typedAttrs.getDimensionPixelOffset(R.styleable.PinEntryEditText_line_spacing, DEFAULT_LINE_SPACING * multi).toFloat()
        numChars = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", DEFAULT_MAX_LENGTH)
        lineStroke = typedAttrs.getDimensionPixelOffset(R.styleable.PinEntryEditText_line_stroke, multi).toFloat()

        textWidths = FloatArray(numChars)

        colorActivated = typedAttrs.getColor(R.styleable.PinEntryEditText_pin_entry_activated, ContextCompat.getColor(context, R.color.color_accent))
        colorFocused = typedAttrs.getColor(R.styleable.PinEntryEditText_pin_entry_focused, ContextCompat.getColor(context, R.color.color_primary))
        colorNotFocused = typedAttrs.getColor(R.styleable.PinEntryEditText_pin_entry_not_focused, ContextCompat.getColor(context, R.color.color_grey))
        colorError = typedAttrs.getColor(R.styleable.PinEntryEditText_pin_entry_error, ContextCompat.getColor(context, R.color.app_red))

        colorText = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "textColor", ContextCompat.getColor(context, R.color.text_primary))

        typedAttrs.recycle()

        disableLongPressMenu()
        setupCursorPositionOnSelection()
        setupTextChangedListener()
    }

    override fun onDraw(canvas: Canvas) {
        val text = textAsString()
        val textLength = text.length
        val availableWidth = width - paddingRight - paddingLeft

        val charSize: Float = if (space < 0) {
            availableWidth / (numChars * 2 - 1).toFloat()
        } else {
            (availableWidth - space * (numChars - 1)) / numChars
        }

        val bottom = (height - paddingBottom).toFloat()
        var startX = paddingLeft.toFloat()

        paint.getTextWidths(text, 0, textLength, textWidths)

        (0 until numChars).forEach { currentChar ->
            paint.strokeWidth = lineStroke

            paint.color = when {
                isError -> colorError
                isFocused && currentChar == textLength -> colorActivated
                isFocused -> colorFocused
                else -> colorNotFocused
            }

            canvas.drawLine(startX, bottom, startX + charSize, bottom, paint)

            if (text.length > currentChar) {
                val middle = startX + charSize / 2
                paint.color = colorText
                canvas.drawText(
                    text,
                    currentChar,
                    currentChar + 1,
                    middle - textWidths[0] / 2,
                    bottom - lineSpacing,
                    paint
                )
            }

            startX += if (space < 0) charSize * 2 else charSize + space
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        throw RuntimeException("setOnClickListener() not supported.")
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            clearFocus()
            hideKeyboard()
        }

        return super.onKeyPreIme(keyCode, event)
    }

    private fun disableLongPressMenu() {
        super.setCustomSelectionActionModeCallback(
            object : ActionMode.Callback {
                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = false

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false
            }
        )
    }

    private fun setupCursorPositionOnSelection() {
        super.setOnClickListener { setSelection(text?.length.orZero()) }
    }

    private fun setupTextChangedListener() {
        addTextChangedListener(
            afterTextChanged = {
                if (it.length == numChars) {
                    hideKeyboard()
                    onMaxLengthReached()
                }
            }
        )
    }
}
