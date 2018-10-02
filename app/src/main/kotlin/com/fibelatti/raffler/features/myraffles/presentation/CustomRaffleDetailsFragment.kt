package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.orZero
import com.fibelatti.raffler.core.extension.setTitle
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BundleDelegate
import kotlinx.android.synthetic.main.fragment_custom_raffle_details.*

private var Bundle.customRaffleId by BundleDelegate.Int("CUSTOM_RAFFLE_ID")

class CustomRaffleDetailsFragment : BaseFragment() {
    companion object {
        fun bundle(
            customRaffleId: Int = 0
        ) = Bundle().apply {
            this.customRaffleId = customRaffleId
        }

        fun navOptions() = navOptions {
            anim {
                enter = R.anim.slide_up
                popExit = R.anim.slide_down
                popEnter = R.anim.fade_in
            }
        }
    }

    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customRaffleDetailsViewModel.run {
            error(error, ::handleError)
            observe(customRaffle, ::showCustomRaffleDetails)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonEdit.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentCustomRaffleDetails_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(customRaffleId = arguments?.customRaffleId.orZero()),
                CreateCustomRaffleFragment.navOptionsEdit()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        customRaffleDetailsViewModel.getCustomRaffleById(arguments?.customRaffleId.orZero())
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        with(customRaffleModel) {
            setTitle(description)
        }
    }
}
