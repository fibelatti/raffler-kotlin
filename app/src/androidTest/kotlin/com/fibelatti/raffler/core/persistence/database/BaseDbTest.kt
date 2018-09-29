package com.fibelatti.raffler.core.persistence.database

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.fibelatti.raffler.BaseTest
import org.junit.After
import org.junit.Before

abstract class BaseDbTest : BaseTest() {
    protected lateinit var appDatabase: AppDatabase

    @Before
    fun initDb() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }
}
