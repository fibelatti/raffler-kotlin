package com.fibelatti.raffler.core.di.modules

import android.content.Context
import androidx.room.Room
import com.fibelatti.raffler.BuildConfig
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.core.persistence.database.DATABASE_NAME
import com.fibelatti.raffler.core.persistence.database.getDatabaseMigrations
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDao
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleItemDao
import com.fibelatti.raffler.features.preferences.data.PreferencesDao
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
            .addMigrations(*getDatabaseMigrations())
            .apply { if (BuildConfig.DEBUG) fallbackToDestructiveMigration() }
            .build()

    @Provides
    @JvmStatic
    fun provideQuickDecisionDao(
        appDatabase: AppDatabase
    ): QuickDecisionDao = appDatabase.getQuickDecisionDao()

    @Provides
    @JvmStatic
    fun provideCustomRaffleDao(
        appDatabase: AppDatabase
    ): CustomRaffleDao = appDatabase.getCustomRaffleDao()

    @Provides
    @JvmStatic
    fun provideCustomRaffleItemDao(
        appDatabase: AppDatabase
    ): CustomRaffleItemDao = appDatabase.getCustomRaffleItemDao()

    @Provides
    @JvmStatic
    fun providePreferencesDao(
        appDatabase: AppDatabase
    ): PreferencesDao = appDatabase.getPreferencesDao()
}
