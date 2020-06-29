package com.fibelatti.raffler.features.home

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.visibleIf
import com.fibelatti.raffler.core.platform.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity : BaseActivity() {

    private val bottomBarEnabledFragments: List<Int> by lazy {
        listOf(
            R.id.fragmentQuickDecision,
            R.id.fragmentQuickDecisionResult,
            R.id.fragmentLottery,
            R.id.fragmentMyRaffles,
            R.id.fragmentPreferences,
            R.id.fragmentPreferencesGeneral,
            R.id.fragmentPreferencesLottery,
            R.id.fragmentPreferencesCustomRaffle
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp() = fragmentHost.findNavController().navigateUp()

    private fun setupNavigation() {
        fragmentHost.findNavController().apply {
            graph = navInflater.inflate(R.navigation.nav_graph)
            layoutBottomNavigation.setupWithNavController(this)
            addOnDestinationChangedListener { _, destination, _ ->
                layoutBottomNavigation.visibleIf(destination.id in bottomBarEnabledFragments)
            }
        }
    }
}
