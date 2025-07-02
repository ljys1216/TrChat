package me.arasple.mc.trchat.util.proxy

import me.arasple.mc.trchat.util.parseString
import me.arasple.mc.trchat.util.proxy.common.MessageBuilder
import java.util.*

fun buildMessage(vararg messages: String): List<ByteArray> {
    return MessageBuilder.create(arrayOf(UUID.randomUUID().parseString(), *messages))
}