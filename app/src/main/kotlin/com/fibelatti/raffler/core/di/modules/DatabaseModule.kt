package com.fibelatti.raffler.core.di.modules

import android.content.Context
import androidx.room.Room
import com.fibelatti.raffler.core.di.AppContext
import com.fibelatti.raffler.core.persistence.database.AppDatabase
import com.fibelatti.raffler.core.persistence.database.AppDatabaseCallback
import com.fibelatti.raffler.core.persistence.database.DATABASE_NAME
import com.fibelatti.raffler.core.persistence.database.getDatabaseMigrations
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDao
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleItemDao
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleVotingDao
import com.fibelatti.raffler.features.preferences.data.PreferencesDao
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@AppContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(*getDatabaseMigrations())
            .addCallback(AppDatabaseCallback)
            .build()

    @Provides
    fun AppDatabase.quickDecisionDao(): QuickDecisionDao = getQuickDecisionDao()

    @Provides
    fun AppDatabase.customRaffleDao(): CustomRaffleDao = getCustomRaffleDao()

    @Provides
    fun AppDatabase.customRaffleItemDao(): CustomRaffleItemDao = getCustomRaffleItemDao()

    @Provides
    fun AppDatabase.customRaffleVotingDao(): CustomRaffleVotingDao = getCustomRaffleVotingDao()

    @Provides
    fun AppDatabase.preferencesDao(): PreferencesDao = getPreferencesDao()
}
