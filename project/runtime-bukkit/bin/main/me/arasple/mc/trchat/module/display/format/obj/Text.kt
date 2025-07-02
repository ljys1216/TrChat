package me.arasple.mc.trchat.module.display.format.obj

import me.arasple.mc.trchat.module.internal.hook.HookPlugin
import me.arasple.mc.trchat.module.internal.script.Condition
import me.arasple.mc.trchat.module.internal.script.kether.KetherHandler
import me.arasple.mc.trchat.util.color.colorify
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
        return text.replaceWithOrder(*vars)
    }

    fun process(sender: CommandSender, color: Boolean = true, vararg vars: String): ComponentText {
        var text = content(sender, *vars)
        if (color) {
            text = text.colorify()
        }
        return if (isDragonCoreHooked) {
            Components.text(text, color = false)
        } else {
            Components.text(text)
        }
    }
}