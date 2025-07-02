package me.arasple.mc.trchat.module.display.channel.obj

import me.arasple.mc.trchat.module.internal.script.Reaction
import org.bukkit.entity.Player
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

/**
 * @author ItsFlicker
 * @since 2022/6/15 18:03
 */
class ChannelEvents(
    private val process: Reaction?,
    private val send: Reaction?,
    private val join: Reaction?,
    private val quit: Reaction?
) {

    fun process(sender: Player, component: ComponentText): ComponentText? {
        process ?: return component
        return when (val result = process.eval(sender, "message" to component.toPlainText())) {
            null -> component
            is Boolean -> component.takeIf { result }
            is String -> Components.text(result)
            else -> component
        }
    }

    fun send(sender: Player, receiver: String, message: String): Boolean {
        send ?: return true
        return when (val result = send.eval(sender, "receiver" to receiver, "message" to message)) {
            null -> true
            is Boolean -> result
            else -> true
        }
    }

    fun join(sender: Player) {
        join?.eval(sender)
    }

    fun quit(sender: Player) {
        quit?.eval(sender)
    }

}