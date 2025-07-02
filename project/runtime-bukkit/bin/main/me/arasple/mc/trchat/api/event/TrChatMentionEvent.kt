package me.arasple.mc.trchat.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class TrChatMentionEvent(
    val sender: Player,
    val receiver: String
) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = true

}