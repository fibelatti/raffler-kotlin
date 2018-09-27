package com.fibelatti.raffler.features.home

import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.exhaustive
import com.fibelatti.raffler.core.extension.inTransaction
import com.fibelatti.raffler.core.platform.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity :
    BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    enum class CurrentView {
        QUICK_DECISIONS, GROUPS, PREFERENCES
    }

    private var selectedItemId: Int = R.id.menuItemQuickDecisions
    private var currentView: CurrentView = CurrentView.QUICK_DECISIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector?.inject(this)

        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setupLayout()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.itemId.takeIf { it != selectedItemId }?.let {
            selectedItemId = it
            item.isChecked = true

            when (it) {
                R.id.menuItemQuickDecisions -> updateContent(CurrentView.QUICK_DECISIONS)
                R.id.menuItemPreferences -> updateContent(CurrentView.PREFERENCES)
            }
        }

        return true
    }

    private fun setupLayout() {
        layoutBottomNavigation.selectedItemId = R.id.menuItemQuickDecisions
        layoutBottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    private fun updateContent(currentView: CurrentView) {
        currentView.takeIf { it != this.currentView }?.let {
            this.currentView = it
            when (it) {
                CurrentView.QUICK_DECISIONS -> {
                }
                CurrentView.GROUPS -> {
                }
                CurrentView.PREFERENCES -> {
                }
            }.exhaustive
        }
    }

    private fun updateLayoutForSelectedItem(
        @StringRes titleId: Int,
        @ColorRes colorId: Int,
        fragment: Fragment
    ) {
        title = getString(titleId)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, colorId))
        layoutBottomNavigation.itemBackgroundResource = colorId

        supportFragmentManager.run {
            inTransaction {
                replace(R.id.layoutFragmentContainer, fragment)
                    .addToBackStack(null)
            }
        }
    }
}
