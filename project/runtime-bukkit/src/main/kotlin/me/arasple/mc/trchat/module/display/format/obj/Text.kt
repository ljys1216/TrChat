package me.arasple.mc.trchat.module.display.format.obj

import me.arasple.mc.trchat.module.internal.hook.HookPlugin
import me.arasple.mc.trchat.module.internal.script.Condition
import me.arasple.mc.trchat.module.internal.script.kether.KetherHandler
import me.arasple.mc.trchat.util.color.colorize
import me.arasple.mc.trchat.util.isDragonCoreHooked
import me.arasple.mc.trchat.util.papiRegex
import me.arasple.mc.trchat.util.setPlaceholders
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components

/**
 * @author ItsFlicker
 * @since 2022/1/21 23:21
 */
class Text(val content: String, val condition: Condition?) {

    val dynamic = papiRegex.containsMatchIn(content)

    fun content(sender: CommandSender, vararg vars: String): String {
        var text = KetherHandler.parseInline(content, sender)
        if (sender is Player) {
            if (dynamic) {
                text = text.setPlaceholders(sender)
            }
            text = HookPlugin.getItemsAdder().replaceFontImages(text, null)
        }
        return text.replaceWithOrder(*vars).colorize(sender)
    }

    fun process(sender: CommandSender, vararg vars: String): ComponentText {
        val text = content(sender, *vars)
        return if (isDragonCoreHooked) {
            Components.text(text, color = false)
        } else {
            Components.text(text)
        }
    }
}