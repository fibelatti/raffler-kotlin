package com.fibelatti.raffler.core.platform.base

import android.app.Dialog
import android.os.Bundle
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.di.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    protected val viewModelProvider: ViewModelProvider
        get() = (activity as BaseActivity).viewModelProvider

    override fun getTheme(): Int = R.style.AppTheme_BottomSheet

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(
        requireContext(),
        theme
    )
}
