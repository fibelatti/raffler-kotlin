package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.preferences.presentation.PreferencesFragment

interface FragmentInjector {
    fun inject(preferencesFragment: PreferencesFragment)
}
