package com.fibelatti.raffler.core.platform

import android.app.Dialog
import android.os.Bundle
import com.fibelatti.raffler.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    protected val injector by lazy { (activity as BaseActivity).injector }
    protected val viewModelFactory by lazy { (activity as BaseActivity).viewModelFactory }

    override fun getTheme(): Int = R.style.AppTheme_BaseBottomSheetDialog_BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}
