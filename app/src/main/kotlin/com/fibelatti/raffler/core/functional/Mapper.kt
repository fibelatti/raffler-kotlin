package com.fibelatti.raffler.core.functional

interface Mapper<InType, OutType> {
    fun map(param: InType): OutType

    fun map(param: List<InType>): List<OutType> = param.map(::map)

    fun mapReverse(param: OutType): InType

    fun mapReverse(param: List<OutType>): List<InType> = param.map(::mapReverse)
}
