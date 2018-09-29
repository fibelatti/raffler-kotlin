package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.core.platform.BaseActivity

interface ActivityInjector {
    fun inject(baseActivity: BaseActivity)
}
