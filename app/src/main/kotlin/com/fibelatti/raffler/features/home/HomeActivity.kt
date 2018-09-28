package com.fibelatti.raffler.features.home

import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity :
    BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector?.inject(this)

        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp() = fragmentHost.findNavController().navigateUp()

    private fun setupNavigation() {
        fragmentHost.findNavController().addOnNavigatedListener { _, destination ->
            when (destination.id) {
                R.id.fragmentQuickDecision -> updateLayoutForSelectedItem(R.string.title_quick_decisions, R.color.color_accent)
                R.id.fragmentPreferences -> updateLayoutForSelectedItem(R.string.title_preferences, R.color.color_gray_dark)
            }
        }
        layoutBottomNavigation.setupWithNavController(fragmentHost.findNavController())
    }

    private fun updateLayoutForSelectedItem(
        @StringRes titleId: Int,
        @ColorRes colorId: Int
    ) {
        title = getString(titleId)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, colorId))
        layoutBottomNavigation.itemBackgroundResource = colorId
    }
}
