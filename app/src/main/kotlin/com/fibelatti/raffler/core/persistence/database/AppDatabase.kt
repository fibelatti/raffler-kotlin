package com.fibelatti.raffler.core.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDao
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDto

const val DATABASE_NAME = "com.fibelatti.raffler.core.persistence.database"
// region Database Versions
const val DATABASE_VERSION_1 = 1
// endregion

@Database(
    entities = [QuickDecisionDto::class],
    version = DATABASE_VERSION_1,
    exportSchema = false
)
@TypeConverters(
    StringListTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getQuickDecisionDao(): QuickDecisionDao
}
