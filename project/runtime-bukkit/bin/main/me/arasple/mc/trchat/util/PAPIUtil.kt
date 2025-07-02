package me.arasple.mc.trchat.util

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.command.CommandSender
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.configuration.ConfigNode
import taboolib.platform.util.sendLang

/**
 * @author Arasple
 * @date 2019/11/29 21:29
 */
@PlatformSide(Platform.BUKKIT)
object PAPIUtil {

    @ConfigNode("Options.Depend-Expansions", "settings.yml")
    var depends = emptyList<String>()
        private set

    fun checkExpansions(sender: CommandSender): Boolean {
        val registered = PlaceholderAPI.getRegisteredIdentifiers()
        val uninstalled = depends.filter { ex -> registered.none { it.equals(ex, true) } }.toTypedArray()
        return if (uninstalled.isEmpty()) {
            true
        } else {
            sender.sendLang("General-Expansions-Header", uninstalled.size)
            uninstalled.forEach { sender.sendLang("General-Expansions-Format", it) }
            false
        }
    }

}