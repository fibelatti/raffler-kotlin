package com.fibelatti.raffler.features.myraffles.presentation.roulette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.fibelatti.core.archcomponents.extension.activityViewModel
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.extension.animateChangingTransitions
import com.fibelatti.core.extension.exhaustive
import com.fibelatti.core.extension.orFalse
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.fragment_custom_raffle_roulette.*
import javax.inject.Inject

class CustomRaffleRouletteFragment @Inject constructor(
    private val customRaffleRouletteDelegate: CustomRaffleRouletteDelegate
) : BaseFragment(R.layout.fragment_custom_raffle_roulette) {

    private val customRaffleDetailsViewModel by activityViewModel {
        viewModelProvider.customRaffleDetailsViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutRoot.animateChangingTransitions()
        layoutTitle.setNavigateUp(R.drawable.ic_close) { findNavController().navigateUp() }


        setupAnimations()
        setupFactory()

        viewLifecycleOwner.observe(customRaffleDetailsViewModel.error, ::handleError)
        viewLifecycleOwner.observeEvent(customRaffleDetailsViewModel.itemsRemaining) {
            if (it == 1) {
                fab?.apply {
                    isEnabled = false
                    text = resources.getQuantityString(R.plurals.custom_raffle_roulette_hint_items_remaining, it, it)
                    setIconResource(0)
                }
            }
        }

        viewLifecycleOwner.observe(customRaffleDetailsViewModel.customRaffle) { customRaffleModel ->
            layoutTitle.setTitle(customRaffleModel.description)
            setupFab(customRaffleModel)

            customRaffleRouletteDelegate.setup(
                context = requireContext(),
                rouletteMusicEnabled = customRaffleDetailsViewModel.rouletteMusicEnabled.value.orFalse()
            ).startRoulette(
                customRaffleModel = customRaffleModel,
                onRouletteStarted = ::onRouletteStarted,
                onRouletteIndexUpdated = { newIndex ->
                    onRouletteIndexUpdated(customRaffleModel, newIndex)
                },
                onRouletteStopped = ::onRouletteStopped
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customRaffleRouletteDelegate.tearDown()
    }

    private fun setupFab(customRaffleModel: CustomRaffleModel) {
        fab.setOnClickListener {
            when (customRaffleRouletteDelegate.status) {
                CustomRaffleRouletteDelegate.RouletteStatus.PLAYING -> {
                    customRaffleRouletteDelegate.stopRoulette()
                    fab.setText(R.string.custom_raffle_roulette_hint_stopping)
                }
                CustomRaffleRouletteDelegate.RouletteStatus.IDLE -> {
                    customRaffleRouletteDelegate.startRoulette(
                        customRaffleModel = customRaffleModel,
                        onRouletteStarted = ::onRouletteStarted,
                        onRouletteIndexUpdated = { newIndex ->
                            onRouletteIndexUpdated(customRaffleModel, newIndex)
                        },
                        onRouletteStopped = ::onRouletteStopped
                    )
                }
                CustomRaffleRouletteDelegate.RouletteStatus.STOPPING -> {
                }
            }.exhaustive
        }
    }

    private fun setupAnimations() {
        textSwitcher.apply {
            inAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.roulette_slide_from_bottom)
            outAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.roulette_slide_to_top)
        }
    }

    private fun setupFactory() {
        textSwitcher.setFactory {
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_roulette_text, textSwitcher, false)
        }
    }

    private fun onRouletteStarted() {
        fab?.apply {
            setText(R.string.custom_raffle_roulette_hint_stop)
            setIconResource(R.drawable.ic_stop)
        }
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun onRouletteIndexUpdated(customRaffleModel: CustomRaffleModel, newIndex: Int) {
        textSwitcher?.setText(customRaffleModel.items[newIndex].description)
    }

    private fun onRouletteStopped(winningIndex: Int) {
        customRaffleDetailsViewModel.itemRaffled(winningIndex)

        fab?.apply {
            setText(R.string.custom_raffle_roulette_hint_play)
            setIconResource(R.drawable.ic_play)
        }
    }
}
