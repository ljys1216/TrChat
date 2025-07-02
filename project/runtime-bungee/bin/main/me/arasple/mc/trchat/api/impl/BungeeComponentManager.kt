package me.arasple.mc.trchat.api.impl

import me.arasple.mc.trchat.TrChat
import me.arasple.mc.trchat.api.ComponentManager
import me.arasple.mc.trchat.util.toUUID
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import taboolib.common.platform.*
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.impl.DefaultComponent
import java.util.*

@PlatformSide(Platform.BUNGEE)
object BungeeComponentManager : ComponentManager {

    init {
        PlatformFactory.registerAPI<ComponentManager>(this)
    }

    override fun sendComponent(receiver: Any, component: ComponentText, sender: Any?) {
        val commandSender = when (receiver) {
            is ProxyCommandSender -> receiver.cast()
            is ProxiedPlayer -> receiver
            else -> error("Unknown receiver type $receiver.")
        }
        val uuid = when (sender) {
            is ProxyPlayer -> sender.uniqueId
            is String -> sender.toUUID()
            is UUID -> sender
            else -> null
        }
        commandSender.sendMessage(uuid, validateComponent(component, 32700).toSpigotObject())
    }

    override fun filterComponent(component: ComponentText, maxLength: Int): ComponentText {
        return validateComponent(DefaultComponent(listOf(filterComponent(component.toSpigotObject()))), maxLength)
    }

    override fun validateComponent(component: ComponentText, maxLength: Int): ComponentText {
        if (maxLength <= 0) return component
        val length = component.toRawMessage().length
        return if (length > maxLength) {
            Components.text("This chat component is too big to show ($length > $maxLength).")
        } else {
            component
        }
    }

    private fun filterComponent(component: BaseComponent): BaseComponent {
        if (component is TextComponent && component.text.isNotEmpty()) {
            component.text = TrChat.api().getFilterManager().filter(component.text).filtered
        }
        if (!component.extra.isNullOrEmpty()) {
            component.extra = component.extra.map { filterComponent(it) }
        }
        return component
    }
}