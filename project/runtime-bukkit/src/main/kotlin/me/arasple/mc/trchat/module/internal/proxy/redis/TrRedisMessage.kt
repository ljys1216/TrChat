package me.arasple.mc.trchat.module.internal.proxy.redis

import me.arasple.mc.trchat.util.ArrayConverter
import taboolib.library.configuration.Conversion

data class TrRedisMessage(
    @Conversion(ArrayConverter::class)
    val data: Array<String> = emptyArray()
)