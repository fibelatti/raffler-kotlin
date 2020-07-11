package com.fibelatti.raffler.features.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fibelatti.core.extension.visibleIf
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity : BaseActivity(R.layout.activity_home) {

    private val bottomBarEnabledFragments: List<Int> = listOf(
        R.id.fragmentQuickDecision,
        R.id.fragmentQuickDecisionResult,
        R.id.fragmentLottery,
        R.id.fragmentMyRaffles,
        R.id.fragmentPreferences,
        R.id.fragmentPreferencesGeneral,
        R.id.fragmentPreferencesLottery,
        R.id.fragmentPreferencesCustomRaffle
    )

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentHost) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp() = navHostFragment.findNavController().navigateUp()

    private fun setupNavigation() {
        navHostFragment.findNavController().apply {
            layoutBottomNavigation.setupWithNavController(this)
            addOnDestinationChangedListener { _, destination, _ ->
                layoutBottomNavigation.visibleIf(
                    predicate = destination.id in bottomBarEnabledFragments,
                    otherwiseVisibility = View.GONE
                )
            }
        }
    }
}
