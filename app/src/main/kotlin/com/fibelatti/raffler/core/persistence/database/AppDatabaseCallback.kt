package com.fibelatti.raffler.core.persistence.database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fibelatti.raffler.features.preferences.data.PREFERENCES_TABLE_INITIAL_SETUP
import java.util.concurrent.Executors

object AppDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        try {
            Executors.newSingleThreadExecutor().execute {
                db.execSQL(PREFERENCES_TABLE_INITIAL_SETUP)
            }
        } catch (e: Exception) {
            Log.d(AppDatabaseCallback::class.java.simpleName, e.message)
        }
    }
}
