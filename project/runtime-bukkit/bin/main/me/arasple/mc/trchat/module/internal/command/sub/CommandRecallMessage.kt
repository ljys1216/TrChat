package me.arasple.mc.trchat.module.internal.command.sub

import me.arasple.mc.trchat.util.session
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.onlinePlayers

/**
 * CommandIgnore
 * me.arasple.mc.trchat.module.internal.command
 *
 * @author ItsFlicker
 * @since 2021/8/11 12:08
 */
@PlatformSide(Platform.BUKKIT)
object CommandRecallMessage {

    val command = subCommand {
        dynamic("message") {
            suggestion<Player> { sender, _ ->
                sender.session.receivedMessages.mapNotNull { it.message }
            }
            execute<Player> { _, _, argument ->
                onlinePlayers.forEach { it.session.removeMessage(argument) }
                onlinePlayers.forEach { it.session.releaseMessage() }
            }
        }
    }
}