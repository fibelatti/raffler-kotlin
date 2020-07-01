package com.fibelatti.raffler.core.platform.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.fibelatti.core.extension.toast
import com.fibelatti.raffler.BuildConfig
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.AppComponentProvider
import com.fibelatti.raffler.core.di.ViewModelProvider
import com.fibelatti.raffler.core.extension.getUpdateContextForLocale
import com.fibelatti.raffler.core.platform.AppConfig

abstract class BaseActivity : AppCompatActivity() {

    val appComponent: AppComponent
        get() = (application as AppComponentProvider).appComponent
    val viewModelProvider: ViewModelProvider
        get() = appComponent

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.getUpdateContextForLocale())
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setupTheme()
        super.onCreate(savedInstanceState)
    }

    fun handleError(error: Throwable) {
        toast(getString(R.string.generic_msg_error))
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
        }
    }

    private fun setupTheme() {
        if (appComponent.currentInstallSharedPreferences().getTheme() == AppConfig.AppTheme.CLASSIC) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
