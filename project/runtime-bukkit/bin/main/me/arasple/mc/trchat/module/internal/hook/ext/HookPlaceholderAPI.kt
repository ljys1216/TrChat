package me.arasple.mc.trchat.module.internal.hook.ext

import me.arasple.mc.trchat.api.event.TrChatEvent
import me.arasple.mc.trchat.module.internal.command.main.CommandMute
import me.arasple.mc.trchat.util.PAPIUtil
import me.arasple.mc.trchat.util.data
import me.arasple.mc.trchat.util.session
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.uncolored
import taboolib.platform.compat.PlaceholderExpansion

/**
 * TrChatPlaceholders
 * me.arasple.mc.trchat.module.internal.hook
 *
 * @author Arasple
 * @since 2021/8/9 23:09
 */
@PlatformSide(Platform.BUKKIT)
object HookPlaceholderAPI : PlaceholderExpansion {

    override val identifier: String
        get() = "trchat"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (player != null && player.isOnline) {
            val params = args.split('_')
            val session = player.session
            val data = player.data
            return when (params[0].lowercase()) {
                "channel" -> session.channel
                "lastpublicmessage", "lastmessage" -> {
                    if (params.getOrNull(1) == "uncolored") session.lastPublicMessage.uncolored()
                    else session.lastPublicMessage
                }
                "lastprivatemessage" -> {
                    if (params.getOrNull(1) == "uncolored") session.lastPrivateMessage.uncolored()
                    else session.lastPrivateMessage
                }
                "toplayer" -> session.lastPrivateTo
                "spy" -> data.isSpying
                "filter" -> data.isFilterEnabled
                "mute" -> data.isMuted
                "mutetime" -> CommandMute.muteDateFormat.format(data.muteTime)
                "mutereason" -> data.muteReason
                "vanish" -> data.isVanishing
                "ignore" -> data.hasIgnored(Bukkit.getOfflinePlayer(params[1]).uniqueId)
                else -> "out of case"
            }.toString()
        }
        return "ERROR"
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onChat(e: TrChatEvent) {
        if (e.forward && !PAPIUtil.checkExpansions(e.session.player)) {
            e.isCancelled = true
        }
    }

}