package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.core.android.BundleDelegate
import com.fibelatti.core.archcomponents.extension.observe
import com.fibelatti.core.archcomponents.extension.observeEvent
import com.fibelatti.core.archcomponents.extension.viewModel
import com.fibelatti.core.extension.addTextChangedListener
import com.fibelatti.core.extension.clearError
import com.fibelatti.core.extension.clearText
import com.fibelatti.core.extension.goneIf
import com.fibelatti.core.extension.hideKeyboard
import com.fibelatti.core.extension.onKeyboardSubmit
import com.fibelatti.core.extension.orFalse
import com.fibelatti.core.extension.showError
import com.fibelatti.core.extension.textAsString
import com.fibelatti.core.extension.visible
import com.fibelatti.core.extension.withItemOffsetDecoration
import com.fibelatti.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.alertDialogBuilder
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.recyclerview.RecyclerViewSwipeToDeleteCallback
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.fragment_create_custom_raffle.*
import javax.inject.Inject

private var Bundle.addAsShortcut by BundleDelegate.Boolean("ADD_AS_SHORTCUT", false)
private var Bundle.customRaffleId by BundleDelegate.Long("CUSTOM_RAFFLE_ID")

class CreateCustomRaffleFragment @Inject constructor(
    private val createCustomRaffleAdapter: CreateCustomRaffleAdapter
) : BaseFragment(R.layout.fragment_create_custom_raffle) {

    companion object {

        fun bundle(
            addAsShortcut: Boolean = false,
            customRaffleId: Long? = null
        ) = Bundle().apply {
            this.addAsShortcut = addAsShortcut
            customRaffleId?.let { this.customRaffleId = it }
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

    private val createCustomRaffleViewModel by viewModel { viewModelProvider.createCustomRaffleViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()

        createCustomRaffleViewModel.run {
            viewLifecycleOwner.observe(error, ::handleError)
            viewLifecycleOwner.observe(showCreateCustomRaffleLayout) { showCustomRaffleNewLayout() }
            viewLifecycleOwner.observe(showEditCustomRaffleLayout, ::showCustomRaffleEditLayout)
            viewLifecycleOwner.observe(customRaffle, ::showCustomRaffleDetails)
            viewLifecycleOwner.observe(addAsQuickDecision, checkboxAddShortcut::setChecked)
            viewLifecycleOwner.observeEvent(showHint) { showAddAsQuickDecisionHint() }
            viewLifecycleOwner.observe(invalidDescriptionError, ::handleInvalidDescriptionError)
            viewLifecycleOwner.observe(invalidItemsQuantityError, ::handleInvalidItemsQuantityError)
            viewLifecycleOwner.observe(invalidItemDescriptionError, ::handleInvalidItemDescriptionError)
            viewLifecycleOwner.observeEvent(onChangedSaved) { handleChangesSaved() }
            viewLifecycleOwner.observeEvent(onDeleted) { handleDeleted() }
        }

        createCustomRaffleViewModel.getCustomRaffleById(arguments?.customRaffleId, arguments?.addAsShortcut)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutRoot.hideKeyboard()
    }

    override fun handleError(error: Throwable) {
        super.handleError(error)
        alertDialogBuilder {
            setMessage(error.message)
            setPositiveButton(R.string.hint_ok) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun setupLayout() {
        layoutTitle.setNavigateUp { findNavController().navigateUp() }

        buttonSave.setOnClickListener {
            if (editTextCustomRaffleItemDescription.textAsString().isNotBlank()) {
                addItem()
            }
            createCustomRaffleViewModel.save(checkboxAddShortcut.isChecked)
        }

        editTextCustomRaffleDescription.addTextChangedListener(
            afterTextChanged = createCustomRaffleViewModel::setDescription
        )

        editTextCustomRaffleItemDescription.apply {
            setOnFocusChangeListener { _, hasFocus -> groupCollapsibleViews.goneIf(hasFocus) }
            onBackPressed = {
                clearFocus()
                hideKeyboard()
                groupCollapsibleViews.visible()
            }
            onKeyboardSubmit {
                addItem()
            }
        }

        imageButtonAddItem.setOnClickListener { addItem() }
        buttonRemoveAll.setOnClickListener { createCustomRaffleViewModel.removeAllItems() }
        buttonDelete.setOnClickListener {
            alertDialogBuilder {
                setMessage(R.string.alert_confirm_deletion)
                setPositiveButton(R.string.hint_yes) { _, _ -> createCustomRaffleViewModel.delete() }
                setNegativeButton(R.string.hint_no) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewItems
            .withLinearLayoutManager()
            .withItemOffsetDecoration(R.dimen.margin_small)
            .adapter = createCustomRaffleAdapter

        val swipeHandler = object : RecyclerViewSwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                createCustomRaffleViewModel.removeItem(viewHolder.adapterPosition)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerViewItems)
    }

    private fun showCustomRaffleNewLayout() {
        layoutTitle.setTitle(R.string.title_create_custom_raffle)
        checkboxAddShortcut.isChecked = arguments?.addAsShortcut.orFalse()
    }

    private fun showCustomRaffleEditLayout(customRaffleModel: CustomRaffleModel) {
        layoutTitle.setTitle(getString(R.string.custom_raffle_edit_title, customRaffleModel.description))
        editTextCustomRaffleDescription.setText(customRaffleModel.description)
        buttonDelete.visible()
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        createCustomRaffleAdapter.submitList(customRaffleModel.items)
    }

    private fun addItem() {
        createCustomRaffleViewModel.addItem(editTextCustomRaffleItemDescription.textAsString())
        editTextCustomRaffleItemDescription.clearText()
    }

    private fun showAddAsQuickDecisionHint() {
        showDismissibleHint(
            container = layoutHintContainer,
            hintTitle = getString(R.string.hint_quick_tip),
            hintMessage = getString(R.string.custom_raffle_add_as_quick_decision_dismissible_hint),
            onHintDismissed = { createCustomRaffleViewModel.hintDismissed() }
        )
    }

    private fun handleInvalidDescriptionError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutCustomRaffleDescription.showError(message)
        } else {
            inputLayoutCustomRaffleDescription.clearError()
        }
    }

    private fun handleInvalidItemsQuantityError(message: String) {
        if (message.isNotBlank()) {
            alertDialogBuilder {
                setMessage(message)
                setPositiveButton(R.string.hint_ok) { dialog, _ -> dialog.dismiss() }
                show()
            }
        }
    }

    private fun handleInvalidItemDescriptionError(message: String) {
        if (message.isNotEmpty()) {
            inputLayoutCustomRaffleItemDescription.showError(message)
        } else {
            inputLayoutCustomRaffleItemDescription.clearError()
        }
    }

    private fun handleChangesSaved() {
        findNavController().navigateUp()
    }

    private fun handleDeleted() {
        findNavController().navigate(
            R.id.fragmentMyRaffles,
            null,
            navOptions { popUpTo = R.id.fragmentQuickDecision }
        )
    }
}
