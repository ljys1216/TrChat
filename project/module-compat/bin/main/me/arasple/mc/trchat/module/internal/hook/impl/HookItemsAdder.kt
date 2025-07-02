package me.arasple.mc.trchat.module.internal.hook.impl

import dev.lone.itemsadder.api.FontImages.FontImageWrapper
import me.arasple.mc.trchat.module.internal.hook.HookAbstract
import org.bukkit.entity.Player

/**
 * @author ItsFlicker
 * @since 2022/2/5 22:30
 */
class HookItemsAdder : HookAbstract() {

    fun replaceFontImages(message: String, player: Player?): String {
        if (!isHooked) {
            return message
        }
        return try {
            if (player == null) {
                FontImageWrapper.replaceFontImages(message)
            } else {
                FontImageWrapper.replaceFontImages(player, message)
            }
        } catch (_: Throwable) {
            message
        }
    }

//    fun replaceFontImages(message: ComponentText, player: Player?): String {
//
//    }
}