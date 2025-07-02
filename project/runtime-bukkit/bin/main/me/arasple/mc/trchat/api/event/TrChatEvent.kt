package me.arasple.mc.trchat.api.event

import me.arasple.mc.trchat.module.display.ChatSession
import me.arasple.mc.trchat.module.display.channel.Channel
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.platform.type.BukkitProxyEvent

/**
 * TrChatEvent
 * me.arasple.mc.trchat.api.event
 *
 * @author ItsFlicker
 * @since 2021/8/20 20:53
 */
class TrChatEvent(
    val channel: Channel,
    val session: ChatSession,
    var component: ComponentText,
    val forward: Boolean = true
) : BukkitProxyEvent() {

    val player = session.player

    var message = component.toPlainText()
        set(value) {
            field = value
            component = Components.text(value)
        }
}