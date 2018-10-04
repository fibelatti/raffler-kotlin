package com.fibelatti.raffler.core.extension

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.fibelatti.raffler.R

fun Fragment.alertDialogBuilder(body: AlertDialog.Builder.() -> Unit) {
    requireContext().alertDialogBuilder(body)
}

fun Context.alertDialogBuilder(body: AlertDialog.Builder.() -> Unit) {
    AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
        .body()
}
