package com.fibelatti.raffler.features.myraffles

import com.fibelatti.raffler.core.functional.Result
import com.fibelatti.raffler.core.functional.Success
import com.fibelatti.raffler.core.functional.UseCase
import com.fibelatti.raffler.core.functional.getOrThrow
import com.fibelatti.raffler.core.functional.runCatching
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModel
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleItemModelMapper
import com.fibelatti.raffler.features.preferences.PreferencesRepository
import javax.inject.Inject

class RememberRaffled @Inject constructor(
    private val customRaffleRepository: CustomRaffleRepository,
    private val preferencesRepository: PreferencesRepository,
    private val customRaffleItemModelMapper: CustomRaffleItemModelMapper
) : UseCase<Unit, RememberRaffled.Params>() {
    override suspend fun run(params: RememberRaffled.Params): Result<Unit> {
        return runCatching {
            return if (preferencesRepository.getPreferences().getOrThrow().rememberRaffledItems) {
                val item = params.customRaffleItemModel
                    .apply { included = params.included }
                    .let(customRaffleItemModelMapper::mapReverse)

                customRaffleRepository.updateCustomRaffleItem(item)
            } else {
                Success(Unit)
            }
        }
    }

    data class Params(val customRaffleItemModel: CustomRaffleItemModel, val included: Boolean)
}
