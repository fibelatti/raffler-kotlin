package com.fibelatti.raffler.core.platform

import android.support.v7.app.AppCompatActivity
import com.fibelatti.raffler.App

abstract class BaseActivity : AppCompatActivity() {

    val injector get() = (application as? App)?.appComponent

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }
}
