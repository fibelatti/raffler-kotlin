package com.fibelatti.raffler.core.di.modules

import androidx.lifecycle.ViewModel
import com.fibelatti.raffler.core.di.modules.viewmodel.ViewModelKey
import com.fibelatti.raffler.features.quickdecision.QuickDecisionRepository
import com.fibelatti.raffler.features.quickdecision.data.QuickDecisionDataSource
import com.fibelatti.raffler.features.quickdecision.presentation.QuickDecisionViewModel
import com.fibelatti.raffler.features.quickdecision.presentation.addnew.AddNewQuickDecisionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class QuickDecisionModule {
    @Binds
    abstract fun bindQuickDecisionRepository(
        quickDecisionDataSource: QuickDecisionDataSource
    ): QuickDecisionRepository

    @Binds
    @IntoMap
    @ViewModelKey(QuickDecisionViewModel::class)
    abstract fun bindQuickDecisionViewModel(quickDecisionViewModel: QuickDecisionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddNewQuickDecisionViewModel::class)
    abstract fun bindAddNewQuickDecisionViewModel(addNewQuickDecisionViewModel: AddNewQuickDecisionViewModel): ViewModel
}
