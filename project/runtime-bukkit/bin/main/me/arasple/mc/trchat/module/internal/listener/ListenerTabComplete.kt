package me.arasple.mc.trchat.module.internal.listener

import org.bukkit.event.player.PlayerCommandSendEvent
import taboolib.common.platform.Ghost
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.ConfigNode

/**
 * ListenerTabComplete
 * me.arasple.mc.trchat.internal.listener
 *
 * @author ItsFlicker
 * @since 2021/10/22 23:25
 */
@PlatformSide(Platform.BUKKIT)
object ListenerTabComplete {
    @ConfigNode("Options.Prevent-Tab-Complete", "settings.yml")
    var enabled = false
        private set

    @Ghost
    @SubscribeEvent
    fun onTab(e: PlayerCommandSendEvent) {
        if (enabled && !e.player.hasPermission("trchat.bypass.tabcomplete")) {
            e.commands.clear()
        }
    }
}