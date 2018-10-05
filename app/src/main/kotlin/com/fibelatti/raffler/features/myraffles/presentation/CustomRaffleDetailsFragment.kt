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
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BundleDelegate
import com.fibelatti.raffler.features.myraffles.presentation.adapter.CustomRaffleDetailsAdapter
import kotlinx.android.synthetic.main.fragment_custom_raffle_details.*
import javax.inject.Inject

private var Bundle.customRaffleId by BundleDelegate.Int("CUSTOM_RAFFLE_ID")

class CustomRaffleDetailsFragment :
    BaseFragment(),
    CustomRaffleModes by CustomRaffleModesDelegate() {

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

    @Inject
    lateinit var adapter: CustomRaffleDetailsAdapter

    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        customRaffleDetailsViewModel.run {
            error(error, ::handleError)
            observe(customRaffle, ::showCustomRaffleDetails)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
        customRaffleDetailsViewModel.getCustomRaffleById(arguments?.customRaffleId.orZero())
    }

    private fun setupLayout() {
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }

        buttonEdit.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentCustomRaffleDetails_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(customRaffleId = arguments?.customRaffleId.orZero()),
                CreateCustomRaffleFragment.navOptionsEdit()
            )
        }

        buttonRaffle.setOnClickListener {
            showRaffleModes(
                requireContext(),
                rouletteClickListener = {
                    layoutRoot.findNavController().navigate(
                        R.id.action_fragmentCustomRaffleDetails_to_fragmentCustomRaffleRoulette,
                        null,
                        CustomRaffleRouletteFragment.navOptions()
                    )
                }
            )
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter

        adapter.clickListener = customRaffleDetailsViewModel::updateItemSelection
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        with(customRaffleModel) {
            layoutTitle.setTitle(description)
            adapter.submitList(items)
        }
    }
}
