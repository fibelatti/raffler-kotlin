package com.fibelatti.raffler.features.randomize

import com.fibelatti.raffler.core.functional.Either
import com.fibelatti.raffler.core.functional.UseCase
import com.fibelatti.raffler.core.functional.runCatching
import java.util.ArrayList
import javax.inject.Inject

class Randomize @Inject constructor() : UseCase<List<Int>, Randomize.Params>() {

    override suspend fun run(params: Params): Either<Throwable, List<Int>> {
        return runCatching {
            with(params) {
                val list = ArrayList<Int>().apply {
                    for (i in 0 until totalQuantity) {
                        add(i)
                    }
                    shuffle()
                }

                if (raffleQuantity > totalQuantity) {
                    list
                } else {
                    ArrayList<Int>().apply {
                        for (i in 0 until raffleQuantity) {
                            add(list[i])
                        }
                    }
                }
            }
        }
    }

    data class Params(val totalQuantity: Int, val raffleQuantity: Int)
}
