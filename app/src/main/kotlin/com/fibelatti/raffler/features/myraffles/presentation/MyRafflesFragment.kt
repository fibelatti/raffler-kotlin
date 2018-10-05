package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.getColorGradientForListSize
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withGridLayoutManager
import com.fibelatti.raffler.core.platform.AddNewModel
import com.fibelatti.raffler.core.platform.BaseFragment
import com.fibelatti.raffler.core.platform.BaseViewType
import com.fibelatti.raffler.features.myraffles.presentation.adapter.CustomRaffleAdapter
import kotlinx.android.synthetic.main.fragment_my_raffles.*
import kotlinx.android.synthetic.main.layout_hint_container.*
import javax.inject.Inject

class MyRafflesFragment : BaseFragment() {

    @Inject
    lateinit var adapter: CustomRaffleAdapter

    private val myRafflesViewModel by lazy {
        viewModelFactory.get<MyRafflesViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        myRafflesViewModel.run {
            error(error, ::handleError)
            observe(customRaffles, ::showCustomRaffles)
            observe(showHintAndCreateNewLayout) { showHintAndCreateNewLayout(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_raffles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
        myRafflesViewModel.getAllCustomRaffles()
    }

    private fun setupLayout() {
        buttonCreateRaffle.setOnClickListener {
            layoutRoot.findNavController().navigate(
                R.id.action_fragmentMyRaffles_to_fragmentCreateCustomRaffle,
                CreateCustomRaffleFragment.bundle(),
                CreateCustomRaffleFragment.navOptionsNew()
            )
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
            .withGridLayoutManager(2)
            .adapter = adapter
    }

    private fun showCustomRaffles(list: List<CustomRaffleModel>) {
        val dataSet = ArrayList<BaseViewType>()
            .apply {
                add(AddNewModel)
                addAll(list)
            }

        adapter.apply {
            addNewClickListener = {
                layoutRoot.findNavController().navigate(
                    R.id.action_fragmentMyRaffles_to_fragmentCreateCustomRaffle,
                    CreateCustomRaffleFragment.bundle(),
                    CreateCustomRaffleFragment.navOptionsNew()
                )
            }
            customRaffleClickListener = {
                layoutRoot.findNavController().navigate(
                    R.id.action_fragmentMyRaffles_to_fragmentCustomRaffleDetails,
                    CustomRaffleDetailsFragment.bundle(customRaffleId = it.toInt()),
                    CustomRaffleDetailsFragment.navOptions()
                )
            }
            colorList = getColorGradientForListSize(
                requireContext(),
                R.color.color_accent,
                R.color.color_primary,
                dataSet.size
            )
            submitList(dataSet)
        }
        recyclerViewItems.visible()
    }

    private fun showHintAndCreateNewLayout(shouldShow: Boolean) {
        if (shouldShow) {
            showDismissibleHint(
                container = layoutHintContainer,
                hintTitle = getString(R.string.my_raffles_hint_title),
                hintMessage = getString(R.string.my_raffles_hint_message)
            )
            buttonCreateRaffle.visible()
            recyclerViewItems.gone()
        } else {
            layoutHintContainer.removeAllViews()
            buttonCreateRaffle.gone()
        }
    }
}
