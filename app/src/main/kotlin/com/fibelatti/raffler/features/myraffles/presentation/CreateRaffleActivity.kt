package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.platform.BaseActivity
import com.fibelatti.raffler.core.platform.BundleDelegate
import kotlinx.android.synthetic.main.activity_create_raffle.*

private var Bundle.addAsShortcut by BundleDelegate.Boolean("ADD_AS_SHORTCUT", false)
private var Bundle.customRaffleId by BundleDelegate.Int("CUSTOM_RAFFLE_ID")

class CreateRaffleActivity : BaseActivity() {

    companion object {
        fun bundle(
            addAsShortcut: Boolean = false,
            customRaffleId: Int = 0
        ) = Bundle().apply {
            this.addAsShortcut = addAsShortcut
            this.customRaffleId = customRaffleId
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_raffle)
        setupLayout()
    }

    private fun setupLayout() {
        buttonCancel.setOnClickListener { finish() }
        checkBoxAddShortcut.isChecked = intent.extras?.addAsShortcut.orFalse()
    }
}
