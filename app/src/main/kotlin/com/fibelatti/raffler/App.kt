package com.fibelatti.raffler

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.DaggerAppComponent
import com.fibelatti.raffler.core.extension.getUpdateContextForLocale

class App : Application() {
    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.getUpdateContextForLocale())
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        appComponent.inject(this)
    }
}
