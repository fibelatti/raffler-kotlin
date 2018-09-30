package com.fibelatti.raffler.features.preferences.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

const val PREFERENCES_DTO_TABLE_NAME = "Preferences"
const val PREFERENCES_DTO_ROULETTE_MUSIC_ENABLED_COLUMN_NAME = "rouletteMusicEnabled"

@Entity(tableName = PREFERENCES_DTO_TABLE_NAME)
data class PreferencesDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val rouletteMusicEnabled: Boolean,
    val hintsDisplayed: MutableMap<String, Boolean>
) {
    @Ignore
    constructor() : this(0, false, mutableMapOf<String, Boolean>())
}
