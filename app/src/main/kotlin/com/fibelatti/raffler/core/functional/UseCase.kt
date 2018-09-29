package com.fibelatti.raffler.core.functional

abstract class UseCase<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Either<Throwable, Type>

    suspend operator fun invoke(params: Params): Either<Throwable, Type> = run(params)

    class None
}
