package com.fibelatti.raffler.features.myraffles.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_my_raffles.*
import kotlinx.android.synthetic.main.layout_hint_container.*

class MyRafflesFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_raffles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
        showDismissibleHint(
            container = layoutHintContainer,
            hintTitle = getString(R.string.my_raffles_hint_title),
            hintMessage = getString(R.string.my_raffles_hint_message)
        )
    }

    private fun setupLayout() {
        buttonCreateRaffle.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMyRaffles_to_activityCreateRaffle)
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems.withDefaultDecoration()
        recyclerViewItems.layoutManager = GridLayoutManager(context, 2)
    }
}
