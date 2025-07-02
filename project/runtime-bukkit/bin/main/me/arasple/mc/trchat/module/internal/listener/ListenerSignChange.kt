package me.arasple.mc.trchat.module.internal.listener

import me.arasple.mc.trchat.TrChat
import me.arasple.mc.trchat.module.adventure.toAdventure
import me.arasple.mc.trchat.module.internal.TrChatBukkit
import me.arasple.mc.trchat.util.color.MessageColors
import me.arasple.mc.trchat.util.parseSimple
import org.bukkit.event.block.SignChangeEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.configuration.ConfigNode

/**
 * @author ItsFlicker
 * @date 2019/8/15 21:18
 */
@PlatformSide(Platform.BUKKIT)
object ListenerSignChange {

    @ConfigNode("Enable.Sign", "filter.yml")
    var filter = true
        private set

    @ConfigNode("Color.Sign", "settings.yml")
    var color = true
        private set

    @ConfigNode("Simple-Component.Sign", "settings.yml")
    var simple = false
        private set

    @Suppress("Deprecation")
    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onSignChange(e: SignChangeEvent) {
        val p = e.player

        for (index in e.lines.indices) {
            if (filter) {
                e.setLine(index, TrChat.api().getFilterManager().filter(e.getLine(index) ?: "", adaptPlayer(p)).filtered)
            }
            if (simple && TrChatBukkit.isPaperEnv && p.hasPermission("trchat.simple.sign")) {
                e.line(index, (e.getLine(index) ?: "").parseSimple().toAdventure())
            } else if (color) {
                e.setLine(index, MessageColors.replaceWithPermission(p, e.getLine(index) ?: "", MessageColors.Type.SIGN))
            }
        }
    }
}