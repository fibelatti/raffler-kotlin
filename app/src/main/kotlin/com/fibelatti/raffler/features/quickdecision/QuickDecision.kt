package com.fibelatti.raffler.features.quickdecision

data class QuickDecision(
    val id: String,
    val locale: String,
    val description: String,
    val values: List<String>
)
