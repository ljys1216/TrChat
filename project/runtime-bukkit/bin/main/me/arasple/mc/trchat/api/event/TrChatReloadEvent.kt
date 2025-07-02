package me.arasple.mc.trchat.api.event

import taboolib.platform.type.BukkitProxyEvent

class TrChatReloadEvent {

    class Function(val functions: MutableList<me.arasple.mc.trchat.module.display.function.Function>) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

    }

    class Channel(val channels: MutableMap<String, me.arasple.mc.trchat.module.display.channel.Channel>) : BukkitProxyEvent() {

        override val allowCancelled: Boolean
            get() = false

    }

}