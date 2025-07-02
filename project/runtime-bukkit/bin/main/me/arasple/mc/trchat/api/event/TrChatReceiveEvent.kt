package me.arasple.mc.trchat.api.event

import me.arasple.mc.trchat.module.display.ChatSession
import me.arasple.mc.trchat.util.session
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.module.chat.ComponentText
import taboolib.platform.type.BukkitProxyEvent
import java.util.*

class TrChatReceiveEvent(
    val receiver: CommandSender,
    var sender: UUID?,
    var message: ComponentText,
    val session: ChatSession? = (receiver as? Player)?.session
): BukkitProxyEvent() {

    val player = session?.player
}