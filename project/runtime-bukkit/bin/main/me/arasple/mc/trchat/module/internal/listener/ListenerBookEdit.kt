package me.arasple.mc.trchat.module.internal.listener

import me.arasple.mc.trchat.util.color.MessageColors
import org.bukkit.event.player.PlayerEditBookEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.ConfigNode

/**
 * @author ItsFlicker
 * @date 2019/8/15 21:18
 */
@PlatformSide(Platform.BUKKIT)
object ListenerBookEdit {

    @ConfigNode("Color.Book", "settings.yml")
    var color = true
        private set

    @Suppress("Deprecation")
    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBookEdit(e: PlayerEditBookEvent) {
        val p = e.player
        val meta = e.newBookMeta
        if (color) {
            meta.pages = MessageColors.replaceWithPermission(p, meta.pages, MessageColors.Type.BOOK)
        }
        e.newBookMeta = meta
    }
}