package com.fibelatti.raffler.features.lottery.presentation

import com.fibelatti.raffler.core.functional.Mapper
import javax.inject.Inject

data class LotteryNumberModel(val value: String)

class LotteryNumberModelMapper @Inject constructor() : Mapper<Int, LotteryNumberModel> {
    override fun map(param: Int): LotteryNumberModel = LotteryNumberModel((param + 1).toString())

    override fun mapReverse(param: LotteryNumberModel): Int = param.value.toInt()
}
