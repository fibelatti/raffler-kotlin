package com.fibelatti.raffler.features.home

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    override fun onSupportNavigateUp() = fragmentHost.findNavController().navigateUp()

    private fun setupNavigation() {
        layoutBottomNavigation.setupWithNavController(fragmentHost.findNavController())
    }
}
