package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.snackbar
import com.fibelatti.raffler.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_custom_raffle_roulette.*
import javax.inject.Inject

class CustomRaffleRouletteFragment : BaseFragment() {

    companion object {
        fun navOptions() = androidx.navigation.navOptions {
            anim {
                enter = R.anim.slide_right_in
                exit = R.anim.slide_left_out
                popEnter = R.anim.slide_left_in
                popExit = R.anim.slide_right_out
            }
        }
    }

    @Inject
    lateinit var rouletteDelegate: CustomRaffleRouletteDelegate

    private val customRaffleDetailsViewModel by lazy {
        viewModelFactory.get<CustomRaffleDetailsViewModel>(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        customRaffleDetailsViewModel.run {
            error(error, ::handleError)
            observe(preparedRaffle) {
                rouletteDelegate.setup(
                    context = requireContext(),
                    customRaffleModel = it,
                    rouletteMusicEnabled = rouletteMusicEnabled.value.orFalse()
                ).startRoulette(
                    onRouletteStarted = ::onRouletteStarted,
                    onRouletteIndexUpdated = ::onRouletteIndexUpdated
                )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_custom_raffle_roulette, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customRaffleDetailsViewModel.preparedRaffle.value?.let { layoutTitle.setTitle(it.description) }
        layoutTitle.navigateUp { layoutRoot.findNavController().navigateUp() }

        setupFab()
        setupAnimations()
        setupFactory()
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rouletteDelegate.tearDown()
    }

    private fun setupFab() {
        fab.setOnClickListener {
            if (rouletteDelegate.isPlaying) {
                rouletteDelegate.stopRoulette(::onRouletteStopped)
                layoutRoot.snackbar(getString(R.string.custom_raffle_roulette_stop_hint))
            } else {
                rouletteDelegate.startRoulette(
                    onRouletteStarted = ::onRouletteStarted,
                    onRouletteIndexUpdated = ::onRouletteIndexUpdated
                )
            }
        }
    }

    private fun setupAnimations() {
        textSwitcher.apply {
            inAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.roulette_slide_from_right)
            outAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.roulette_slide_to_left)
        }
    }

    private fun setupFactory() {
        textSwitcher.setFactory {
            TextView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                gravity = Gravity.CENTER
                textSize = context.resources.getDimension(R.dimen.text_size_regular)
                setTextColor(ContextCompat.getColor(context, R.color.color_accent))
            }
        }
    }

    private fun onRouletteStarted() {
        fab?.setImageResource(R.drawable.ic_stop)
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun onRouletteIndexUpdated(newIndex: Int) {
        customRaffleDetailsViewModel.preparedRaffle.value?.let {
            textSwitcher?.setText(it.items[newIndex].description)
        }
    }

    private fun onRouletteStopped() {
        fab?.setImageResource(R.drawable.ic_play)
    }
}
