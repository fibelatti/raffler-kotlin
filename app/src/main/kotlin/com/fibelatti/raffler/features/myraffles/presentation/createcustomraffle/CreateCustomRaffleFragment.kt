package com.fibelatti.raffler.features.myraffles.presentation.createcustomraffle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.addTextChangedListener
import com.fibelatti.raffler.core.extension.alertDialogBuilder
import com.fibelatti.raffler.core.extension.clearError
import com.fibelatti.raffler.core.extension.clearText
import com.fibelatti.raffler.core.extension.error
import com.fibelatti.raffler.core.extension.gone
import com.fibelatti.raffler.core.extension.hideKeyboard
import com.fibelatti.raffler.core.extension.isKeyboardSubmit
import com.fibelatti.raffler.core.extension.observe
import com.fibelatti.raffler.core.extension.observeEvent
import com.fibelatti.raffler.core.extension.orFalse
import com.fibelatti.raffler.core.extension.showError
import com.fibelatti.raffler.core.extension.textAsString
import com.fibelatti.raffler.core.extension.visible
import com.fibelatti.raffler.core.extension.withDefaultDecoration
import com.fibelatti.raffler.core.extension.withLinearLayoutManager
import com.fibelatti.raffler.core.platform.BundleDelegate
import com.fibelatti.raffler.core.platform.base.BaseFragment
import com.fibelatti.raffler.core.platform.recyclerview.RecyclerViewSwipeToDeleteCallback
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import kotlinx.android.synthetic.main.fragment_create_custom_raffle.*
import javax.inject.Inject

private var Bundle.addAsShortcut by BundleDelegate.Boolean("ADD_AS_SHORTCUT", false)
private var Bundle.customRaffleId by BundleDelegate.Long("CUSTOM_RAFFLE_ID")

class CreateCustomRaffleFragment : BaseFragment() {

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

    @Inject
    lateinit var adapter: CreateCustomRaffleAdapter

    private val createCustomRaffleViewModel by lazy {
        viewModelFactory.get<CreateCustomRaffleViewModel>(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        createCustomRaffleViewModel.run {
            error(error, ::handleError)
            observe(showCreateCustomRaffleLayout) { showCustomRaffleNewLayout() }
            observe(showEditCustomRaffleLayout) { showCustomRaffleEditLayout() }
            observe(customRaffle, ::showCustomRaffleDetails)
            observe(addAsQuickDecision) { checkboxAddShortcut.isChecked = it }
            observeEvent(showHint) { showAddAsQuickDecisionHint() }
            observe(invalidDescriptionError, ::handleInvalidDescriptionError)
            observe(invalidItemsQuantityError, ::handleInvalidItemsQuantityError)
            observe(invalidItemDescriptionError, ::handleInvalidItemDescriptionError)
            observeEvent(onChangedSaved) { handleChangesSaved() }
            observeEvent(onDeleted) { handleDeleted() }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_custom_raffle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupRecyclerView()
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
        layoutTitle.setNavigateUp { layoutRoot.findNavController().navigateUp() }

        buttonSave.setOnClickListener { createCustomRaffleViewModel.save(checkboxAddShortcut.isChecked) }

        editTextCustomRaffleDescription.addTextChangedListener(afterTextChanged = createCustomRaffleViewModel::setDescription)

        editTextCustomRaffleItemDescription.apply {
            setOnFocusChangeListener { _, hasFocus -> if (hasFocus) groupCollapsibleViews.gone() }
            onBackPressed = {
                clearFocus()
                hideKeyboard()
                groupCollapsibleViews.visible()
            }
            setOnEditorActionListener { _, actionId, event ->
                return@setOnEditorActionListener if (isKeyboardSubmit(actionId, event)) {
                    addItem()
                    true
                } else {
                    false
                }
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
        recyclerViewItems.withDefaultDecoration()
            .withLinearLayoutManager()
            .adapter = adapter

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

    private fun showCustomRaffleEditLayout() {
        createCustomRaffleViewModel.customRaffle.value?.let {
            layoutTitle.setTitle(getString(R.string.custom_raffle_edit_title, it.description))
            editTextCustomRaffleDescription.setText(it.description)
            buttonDelete.visible()
        }
    }

    private fun showCustomRaffleDetails(customRaffleModel: CustomRaffleModel) {
        adapter.setItems(customRaffleModel.items.toList())
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
        layoutRoot.findNavController().navigateUp()
    }

    private fun handleDeleted() {
        layoutRoot.findNavController().navigate(
            R.id.fragmentMyRaffles,
            null,
            navOptions { popUpTo = R.id.fragmentQuickDecision }
        )
    }
}
