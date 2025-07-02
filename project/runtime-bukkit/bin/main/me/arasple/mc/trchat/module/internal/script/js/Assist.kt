package me.arasple.mc.trchat.module.internal.script.js

import me.arasple.mc.trchat.util.setPlaceholders
import org.bukkit.entity.Player

/**
 * Assist
 * me.arasple.mc.trchat.internal.script.js
 *
 * @author ItsFlicker
 * @since 2021/8/27 16:44
 */
class Assist {

    companion object {

        val INSTANCE = Assist()
    }

    fun parsePlaceholders(player: Player, string: String): String {
        return string.setPlaceholders(player)
    }
}