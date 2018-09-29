package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.onSupportNavigateUpGoBack
import com.fibelatti.raffler.core.extension.setupToolbar
import com.fibelatti.raffler.core.platform.BaseActivity
import kotlinx.android.synthetic.main.layout_toolbar_default.*

class CreateRaffleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_raffle)
        setupToolbar(toolbar, displayHomeAsUpEnabled = true)
    }

    override fun onSupportNavigateUp() = onSupportNavigateUpGoBack()
}
