package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.preferences.presentation.PreferencesFragment
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionFragment

interface FragmentInjector {
    fun inject(quickDecisionFragment: QuickDecisionFragment)

    fun inject(preferencesFragment: PreferencesFragment)
}
