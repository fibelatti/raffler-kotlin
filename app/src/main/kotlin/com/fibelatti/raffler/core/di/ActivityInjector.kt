package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.features.home.HomeActivity

interface ActivityInjector {
    fun inject(homeActivity: HomeActivity)
}
