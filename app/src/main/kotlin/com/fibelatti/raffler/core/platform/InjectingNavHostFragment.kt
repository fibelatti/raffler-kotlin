package com.fibelatti.raffler.core.platform

import android.os.Bundle
import androidx.annotation.Keep
import androidx.navigation.fragment.NavHostFragment
import com.fibelatti.raffler.core.platform.base.BaseActivity

@Keep
class InjectingNavHostFragment : NavHostFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = (requireActivity() as BaseActivity).activityComponent
            .fragmentFactory()
        super.onCreate(savedInstanceState)
    }
}
