package com.fibelatti.raffler.core.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDao
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleDto
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleItemDao
import com.fibelatti.raffler.features.myraffles.data.CustomRaffleItemDto
import com.fibelatti.raffler.features.preferences.data.PreferencesDao
import com.fibelatti.raffler.features.preferences.data.PreferencesDto
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDao
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDto

const val DATABASE_NAME = "com.fibelatti.raffler.db"
// region Database Versions
const val DATABASE_VERSION_1 = 1
const val DATABASE_VERSION_2 = 2
const val DATABASE_VERSION_3 = 3
const val DATABASE_VERSION_4 = 4
const val DATABASE_VERSION_5 = 5
// endregion

@Database(
    entities = [
        QuickDecisionDto::class,
        CustomRaffleDto::class,
        CustomRaffleItemDto::class,
        PreferencesDto::class
    ],
    version = DATABASE_VERSION_5,
    exportSchema = false
)
@TypeConverters(
    StringListTypeConverter::class,
    MapStringBooleanTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getQuickDecisionDao(): QuickDecisionDao
    abstract fun getCustomRaffleDao(): CustomRaffleDao
    abstract fun getCustomRaffleItemDao(): CustomRaffleItemDao
    abstract fun getPreferencesDao(): PreferencesDao
}
