package com.fibelatti.raffler.core.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fibelatti.raffler.core.di.AppContext
import com.fibelatti.raffler.core.extension.getUserPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import java.text.Collator
import java.util.Locale

@Module
abstract class CoreModule {

    companion object {

        @Provides
        fun gson(): Gson = GsonBuilder().create()

        @Provides
        fun localeDefault(): Locale = Locale.getDefault()

        @Provides
        fun collatorUs(): Collator = Collator.getInstance(Locale.US)

        @Provides
        fun userSharedPreferences(@AppContext context: Context): SharedPreferences = context.getUserPreferences()
    }

    @AppContext
    @Binds
    abstract fun bindContext(app: Application): Context
}
