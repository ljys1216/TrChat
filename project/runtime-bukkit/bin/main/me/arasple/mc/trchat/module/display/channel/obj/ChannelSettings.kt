package me.arasple.mc.trchat.module.display.channel.obj

import me.arasple.mc.trchat.module.internal.script.Condition

/**
 * @author ItsFlicker
 * @since 2022/2/5 13:25
 */
class ChannelSettings(
    val joinPermission: String = "",
    val listenPermission: String = "",
    val speakCondition: Condition = Condition.EMPTY,
    val alwaysListen: Boolean = true,
    val isPrivate: Boolean = false,
    val range: ChannelRange = ChannelRange(ChannelRange.Type.ALL, -1),
    val proxy: Boolean = false,
    val forceProxy: Boolean = false,
    val doubleTransfer: Boolean = true,
    val ports: List<Int> = emptyList(),
    val disabledFunctions: List<String> = emptyList(),
    val filterBeforeSending: Boolean = false,
    val sendToDiscord: Boolean = true,
    val receiveFromDiscord: Boolean = true,
    val discordChannel: String = ""
)