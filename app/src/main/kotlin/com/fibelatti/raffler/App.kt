package com.fibelatti.raffler

import android.app.Application
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.AppComponentProvider
import com.fibelatti.raffler.core.di.DaggerAppComponent

class App : Application(), AppComponentProvider {

    override val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(application = this)
    }
}
