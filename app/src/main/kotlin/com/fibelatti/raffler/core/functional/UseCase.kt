package com.fibelatti.raffler.core.functional

abstract class UseCase<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Result<Type>

    suspend operator fun invoke(params: Params): Result<Type> = run(params)

    class None
}
