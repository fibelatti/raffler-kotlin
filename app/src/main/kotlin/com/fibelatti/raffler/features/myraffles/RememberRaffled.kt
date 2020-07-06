package com.fibelatti.raffler.features.myraffles

import com.fibelatti.core.functional.Result
import com.fibelatti.core.functional.Success
import com.fibelatti.core.functional.UseCaseWithParams
import com.fibelatti.core.functional.catching
import com.fibelatti.core.functional.getOrThrow
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class RememberRaffled @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleItemModelMapper: CustomRaffleItemModelMapper
) : UseCaseWithParams<Unit, RememberRaffled.Params>() {

    override suspend fun run(params: Params): Result<Unit> = catching {
        return if (preferencesRepository.getPreferences().getOrThrow().rememberRaffledItems) {
            val item = params.customRaffleItemModel
                .copy(included = params.included)
                .let(customRaffleItemModelMapper::mapReverse)

            customRaffleRepository.updateCustomRaffleItem(item)
        } else {
            Success(Unit)
        }
    }

    data class Params(val customRaffleItemModel: CustomRaffleItemModel, val included: Boolean)
}
