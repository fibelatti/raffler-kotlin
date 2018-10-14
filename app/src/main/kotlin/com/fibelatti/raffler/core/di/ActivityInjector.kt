package com.fibelatti.raffler.core.di

import com.fibelatti.raffler.core.platform.base.BaseActivity

interface ActivityInjector {
    fun inject(baseActivity: BaseActivity)
}
