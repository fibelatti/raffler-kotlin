package com.fibelatti.raffler.features.lottery.presentation

import com.fibelatti.core.functional.TwoWayMapper
import javax.inject.Inject

data class LotteryNumberModel(val value: String)

class LotteryNumberModelMapper @Inject constructor() : TwoWayMapper<Int, LotteryNumberModel> {

    override fun map(param: Int): LotteryNumberModel = LotteryNumberModel("$param")

    override fun mapReverse(param: LotteryNumberModel): Int = param.value.toInt()
}
