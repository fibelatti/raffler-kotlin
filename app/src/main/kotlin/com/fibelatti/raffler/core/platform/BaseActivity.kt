package com.fibelatti.raffler.core.platform

import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.fibelatti.raffler.App
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelFactory
import com.fibelatti.raffler.features.preferences.data.getDarkModeEnabled
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    val injector get() = (application as App).appComponent

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        setupTheme()
        super.onCreate(savedInstanceState)
    }

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }

    private fun setupTheme() {
        if (sharedPreferences.getDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
