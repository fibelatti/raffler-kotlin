package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BundleDelegate
import kotlinx.android.synthetic.main.fragment_create_custom_raffle.*

private var Bundle.addAsShortcut by BundleDelegate.Boolean("ADD_AS_SHORTCUT", false)
private var Bundle.customRaffleId by BundleDelegate.Int("CUSTOM_RAFFLE_ID")

class CreateCustomRaffleFragment : BaseFragment() {

    companion object {
        fun bundle(
            addAsShortcut: Boolean = false,
            customRaffleId: Int = 0
        ) = Bundle().apply {
            this.addAsShortcut = addAsShortcut
            this.customRaffleId = customRaffleId
        }

        fun navOptionsNew() = navOptions {
            anim {
                enter = R.anim.slide_up
                popExit = R.anim.slide_down
                popEnter = R.anim.fade_in
            }
        }

        fun navOptionsEdit() = navOptions {
            anim {
                enter = R.anim.slide_right_in
                exit = R.anim.slide_left_out
                popEnter = R.anim.slide_left_in
                popExit = R.anim.slide_right_out
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_custom_raffle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        buttonCancel.setOnClickListener { layoutRoot.findNavController().navigateUp() }
        checkBoxAddShortcut.isChecked = arguments?.addAsShortcut.orFalse()
    }
}
