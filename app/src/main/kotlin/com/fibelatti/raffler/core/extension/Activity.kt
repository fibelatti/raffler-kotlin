package com.fibelatti.raffler.core.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

fun AppCompatActivity.setupToolbar(
    toolbar: Toolbar,
    displayShowTitleEnabled: Boolean = true,
    displayHomeAsUpEnabled: Boolean = false
) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        setDisplayShowTitleEnabled(displayShowTitleEnabled)
        setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
    }
}

fun AppCompatActivity.onSupportNavigateUpGoBack(): Boolean {
    onBackPressed()
    return true
}
