package com.fibelatti.raffler.core.platform

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.fibelatti.raffler.App
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelFactory
import com.fibelatti.raffler.core.persistence.CurrentInstallSharedPreferences
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    val injector get() = (application as App).appComponent

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var currentInstallSharedPreferences: CurrentInstallSharedPreferences

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        setupTheme()
        super.onCreate(savedInstanceState)
    }

    fun handleError(error: Throwable) {
        error.printStackTrace()
    }

    fun showUpNavigation(predicate: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(predicate)
    }

    private fun setupTheme() {
        if (currentInstallSharedPreferences.getTheme() == AppConfig.AppTheme.CLASSIC) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
