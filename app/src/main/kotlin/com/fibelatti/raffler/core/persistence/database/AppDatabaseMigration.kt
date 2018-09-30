package com.fibelatti.raffler.core.persistence.database

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun getDatabaseMigrations(): Array<Migration> =
    arrayOf(
        MigrationFrom1To2,
        MigrationFrom2To3,
        MigrationFrom3To4,
        MigrationFrom4To5
    )

// region Raffler 1 database versions
object MigrationFrom1To2 : Migration(DATABASE_VERSION_1, DATABASE_VERSION_2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        logMigration()
    }
}

object MigrationFrom2To3 : Migration(DATABASE_VERSION_2, DATABASE_VERSION_3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        logMigration()
    }
}

object MigrationFrom3To4 : Migration(DATABASE_VERSION_3, DATABASE_VERSION_4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        logMigration()
    }
}
// endregion

object MigrationFrom4To5 : Migration(DATABASE_VERSION_4, DATABASE_VERSION_5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        logMigration()

        // region Migration helpers
        fun migrateGroupsToCustomRaffle() {
            try {
                // Migrate old data
                database.execSQL("INSERT INTO `CustomRaffle` " +
                    "SELECT _id, group_name FROM groups")
                database.execSQL("INSERT INTO `CustomRaffleItem` " +
                    "SELECT _id, group_id, item_name FROM group_items")

                // Drop old tables
                database.execSQL("DROP TABLE IF EXISTS groups")
                database.execSQL("DROP TABLE IF EXISTS group_items")

                logLegacySuccess()
            } catch (e: Exception) {
                logLegacyError()
            }
        }

        fun migrateSettingsToPreferences() {
            try {
                // Migrate old data
                database.execSQL("INSERT INTO `Preferences`" +
                    "SELECT 1, roulette_music_enabled, '' FROM settings")

                // Drop old table
                database.execSQL("DROP TABLE IF EXISTS settings")

                logLegacySuccess()
            } catch (e: Exception) {
                logLegacyError()

                database.execSQL("INSERT INTO `Preferences` VALUES (1, 0, '')")
            }
        }

        fun createNewTables() {
            database.execSQL("CREATE TABLE IF NOT EXISTS `QuickDecision` (`id` TEXT NOT NULL, " +
                "`locale` TEXT NOT NULL, `description` TEXT NOT NULL, `values` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`, `locale`))")

            database.execSQL("CREATE TABLE IF NOT EXISTS `CustomRaffle` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `description` TEXT NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `CustomRaffleItem` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customRaffleId` INTEGER NOT NULL, " +
                "`description` TEXT NOT NULL, FOREIGN KEY(`customRaffleId`) REFERENCES " +
                "`CustomRaffle`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")

            database.execSQL("CREATE TABLE IF NOT EXISTS `Preferences` " +
                "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`rouletteMusicEnabled` INTEGER NOT NULL, " +
                "`hintsDisplayed` TEXT NOT NULL)")
        }
        // endregion

        // region Actual migration steps
        database.execSQL("DROP TABLE IF EXISTS `quick_decision`")
        createNewTables()
        migrateSettingsToPreferences()
        migrateGroupsToCustomRaffle()
        // endregion
    }
}

// region Logcat
private fun Migration.tag(): String = javaClass.simpleName

private fun Migration.logMigration() {
    Log.d(tag(), "Running migration ${tag()}")
}

private fun Migration.logLegacyError() {
    Log.d(tag(), "Legacy table not found: no action required")
}

private fun Migration.logLegacySuccess() {
    Log.d(tag(), "Legacy table found: data migrated successfully")
}
// endregion
