package com.fibelatti.raffler.features.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fibelatti.core.extension.doOnApplyWindowInsets
import com.fibelatti.core.extension.gone
import com.fibelatti.core.extension.snackbar
import com.fibelatti.core.extension.visible
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity : BaseActivity(R.layout.activity_home) {

    companion object {

        private const val FLEXIBLE_UPDATE_REQUEST = 1001
    }

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

    private val inAppUpdateManager get() = activityComponent.inAppUpdateManager()

    private var topInset: Int = 0
    private var bottomInset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setupView()
        setupNavigation()
        inAppUpdateManager.checkForAvailableUpdates(
            this,
            FLEXIBLE_UPDATE_REQUEST,
            ::onUpdateDownloadComplete
        )
    }

    override fun onSupportNavigateUp() = navHostFragment.findNavController().navigateUp()

    private fun setupView() {
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        coordinatorLayout.doOnApplyWindowInsets { view, insets, initialPadding, _ ->
            topInset = insets.systemWindowInsetTop

            ViewCompat.setPaddingRelative(
                view,
                initialPadding.start,
                initialPadding.top + insets.systemWindowInsetTop,
                initialPadding.end,
                initialPadding.bottom)

            // Remove listener after initial setup
            view.doOnApplyWindowInsets { _, _, _, _ -> }
        }

        layoutBottomNavigation.doOnApplyWindowInsets { view, insets, padding, _ ->
            bottomInset = insets.systemWindowInsetBottom

            ViewCompat.setPaddingRelative(
                view,
                padding.start,
                padding.top,
                padding.end,
                padding.bottom + insets.systemWindowInsetBottom
            )

            // Remove listener after initial setup
            view.doOnApplyWindowInsets { _, _, _, _ -> }
        }
    }

    private fun setupNavigation() {
        navHostFragment.findNavController().apply {
            layoutBottomNavigation.setupWithNavController(this)
            addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id in bottomBarEnabledFragments) {
                    ViewCompat.setPaddingRelative(coordinatorLayout, 0, topInset, 0, 0)
                    layoutBottomNavigation.visible()
                } else {
                    ViewCompat.setPaddingRelative(coordinatorLayout, 0, topInset, 0, bottomInset)
                    layoutBottomNavigation.gone()
                }
            }
        }
    }

    private fun onUpdateDownloadComplete() {
        layoutRoot.snackbar(
            message = getString(R.string.in_app_update_ready),
            textColor = R.color.text_primary,
            marginSize = R.dimen.margin_regular,
            background = R.drawable.background_snackbar,
            duration = Snackbar.LENGTH_LONG
        ) {
            setAction(R.string.in_app_update_install) { inAppUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(layoutRoot.context, R.color.color_on_background))
        }
    }
}
