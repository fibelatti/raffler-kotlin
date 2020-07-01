package com.fibelatti.raffler

import android.app.Application
import android.content.Context
import com.fibelatti.raffler.core.di.AppComponent
import com.fibelatti.raffler.core.di.AppComponentProvider
import com.fibelatti.raffler.core.di.DaggerAppComponent
import com.fibelatti.raffler.core.extension.getUpdateContextForLocale

class App : Application(), AppComponentProvider {

    override val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory()
            .create(application = this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.getUpdateContextForLocale())
    }
}
