package com.fibelatti.raffler.core.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import com.fibelatti.raffler.BuildConfig
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.core.persistence.database.DATABASE_NAME
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PersistenceModule {
    @Provides
    @Singleton
    @JvmStatic
    fun providesDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .apply { if (BuildConfig.DEBUG) fallbackToDestructiveMigration() }
            .build()

    @Provides
    @JvmStatic
    fun provideQuickDecisionDao(
        appDatabase: AppDatabase
    ): QuickDecisionDao = appDatabase.getQuickDecisionDao()
}
