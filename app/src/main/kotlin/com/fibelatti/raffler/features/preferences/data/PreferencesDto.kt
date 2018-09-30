package com.fibelatti.raffler.features.preferences.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

const val PREFERENCES_DTO_TABLE_NAME = "Preferences"

@Entity(tableName = PREFERENCES_DTO_TABLE_NAME)
data class PreferencesDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val rouletteMusicEnabled: Boolean,
    val hintsDisplayed: Map<String, Boolean>
) {
    @Ignore
    constructor() : this(0, false, mapOf())
}
