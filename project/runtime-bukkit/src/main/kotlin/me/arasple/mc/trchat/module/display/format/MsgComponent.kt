package me.arasple.mc.trchat.module.display.format

import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.module.display.format.obj.Style
import me.arasple.mc.trchat.module.display.format.obj.Style.Companion.applyTo
import me.arasple.mc.trchat.module.display.function.Function
import me.arasple.mc.trchat.module.internal.script.Condition
import me.arasple.mc.trchat.util.color.CustomColor
import me.arasple.mc.trchat.util.color.MessageColors
import me.arasple.mc.trchat.util.isDragonCoreHooked
import me.arasple.mc.trchat.util.pass
import me.arasple.mc.trchat.util.session
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.util.VariableReader
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.impl.AdventureComponent

/**
 * @author ItsFlicker
 * @since 2021/12/12 13:46
 */
class MsgComponent(val defaultColor: List<Pair<CustomColor, Condition?>>, style: List<Style>) : JsonComponent(null, style) {

    fun processComponent(sender: CommandSender, msg: TextComponent, disabledFunctions: List<String>): TextComponent {
        val children = msg.children().map { processComponent(sender, it as TextComponent, disabledFunctions) }
        val new = createComponent(sender, msg.content(), disabledFunctions).toAdventureObject() as TextComponent
        val style = new.style().merge(msg.style())
        return new.children(new.children() + children).style(style)
    }

    fun createComponent(sender: CommandSender, msg: ComponentText, disabledFunctions: List<String>): ComponentText {
        if (!Components.useAdventure) {
            return createComponent(sender, msg.toPlainText(), disabledFunctions)
        }
        val component = msg.toAdventureObject() as TextComponent
        return AdventureComponent(processComponent(sender, component, disabledFunctions))
    }

    fun createComponent(sender: CommandSender, msg: String, disabledFunctions: List<String>): ComponentText {
        if (msg.isBlank()) return Components.empty()
        var message = msg.replace("{{", "\\{{")

        // 非玩家 不处理functions
        if (sender !is Player) {
            val defaultColor = defaultColor[0].first
            return toTextComponent(sender, defaultColor.colored(sender, message))
        }

        val component = Components.empty()

        // 创建{{xxx:xxx}}
        Function.functions.filter { it.alias !in disabledFunctions && it.canUse(sender) }.forEach {
            message = it.createVariable(sender, message)
        }

        val defaultColor = sender.session.getColor(defaultColor.firstOrNull { it.second.pass(sender) }?.first)
        val useMiniMessage = Settings.miniMessage && sender.hasPermission("trchat.color.minimessage")

        // 分割为多个ComponentText
        for (part in parser.readToFlatten(message)) {
            if (part.isVariable) {
                val args = part.text.split(":", limit = 2)
                val function = Function.functions.firstOrNull { it.id == args[0] }
                if (function != null) {
                    function.parseVariable(sender, args[1])?.let { component.append(it) }
                    function.reaction?.eval(sender, "message" to message)
                }
                continue
            }
            if (useMiniMessage) {
                component.append(AdventureComponent(MiniMessage.miniMessage().deserialize(part.text)))
            } else {
                component.append(toTextComponent(sender, defaultColor.colored(sender, part.text)))
            }
        }
        return component
    }

    override fun toTextComponent(sender: CommandSender, vararg vars: String): ComponentText {
        val message = vars[0]
        val component = if (isDragonCoreHooked) {
            Components.text(message, color = false)
        } else {
            Components.text(message)
        }
        style.forEach {
            it.applyTo(component, sender, *vars)
        }
        return component
    }

    companion object {

        private val parser = VariableReader()
    }
}