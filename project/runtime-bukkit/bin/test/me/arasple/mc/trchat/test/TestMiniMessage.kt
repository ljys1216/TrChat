package me.arasple.mc.trchat.test

import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.module.display.format.MsgComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import taboolib.module.chat.Components
import taboolib.module.chat.impl.AdventureComponent
import taboolib.platform.BukkitPlugin
import taboolib.module.configuration.Configuration

class TestMiniMessage {

    companion object {

        private lateinit var player: Player

        @BeforeAll
        @JvmStatic
        fun setup() {
            MockTrChat.mockPlugin()
            MockTrChat.mockServer()
            player = MockTrChat.mockPlayer("testPlayer")
            val permissions = player.addAttachment(BukkitPlugin.getInstance())
            permissions.setPermission("trchat.color.minimessage", true)
            Settings.conf = Configuration.empty()
        }
    }

    @Test
    fun `test minimessage parsing enabled`() {
        Settings.conf.set("Color.MiniMessage", true)
        val msgComponent = MsgComponent(emptyList(), emptyList())
        val message = "<gold>Hello, <blue>@testPlayer</blue>! [item]"
        val component = msgComponent.createComponent(player, message, emptyList())
        val serialized = MiniMessage.miniMessage().serialize((component as AdventureComponent).toAdventureObject())

        // 验证 MiniMessage 是否被正确解析
        assertTrue(serialized.contains("<gold>"))
        // 验证 @提及 是否仍然有效
        assertTrue(serialized.contains("@testPlayer"))
        // 验证 [item] 是否仍然有效
        assertTrue(serialized.contains("[item]"))
    }

    @Test
    fun `test minimessage parsing disabled`() {
        Settings.conf.set("Color.MiniMessage", false)
        val msgComponent = MsgComponent(emptyList(), emptyList())
        val message = "<gold>Hello, <blue>@testPlayer</blue>! [item]"
        val component = msgComponent.createComponent(player, message, emptyList())
        val plain = component.toPlainText()

        // 验证 MiniMessage 是否未被解析
        assertFalse(plain.contains("§6"))
        // 验证 @提及 是否仍然有效
        assertTrue(plain.contains("@testPlayer"))
        // 验证 [item] 是否仍然有效
        assertTrue(plain.contains("[item]"))
    }

    @Test
    fun `test minimessage parsing no permission`() {
        Settings.conf.set("Color.MiniMessage", true)
        val permissions = player.addAttachment(BukkitPlugin.getInstance())
        permissions.setPermission("trchat.color.minimessage", false)

        val msgComponent = MsgComponent(emptyList(), emptyList())
        val message = "<gold>Hello, <blue>@testPlayer</blue>! [item]"
        val component = msgComponent.createComponent(player, message, emptyList())
        val plain = component.toPlainText()

        // 验证 MiniMessage 是否未被解析
        assertFalse(plain.contains("§6"))
        // 验证 @提及 是否仍然有效
        assertTrue(plain.contains("@testPlayer"))
        // 验证 [item] 是否仍然有效
        assertTrue(plain.contains("[item]"))

        permissions.setPermission("trchat.color.minimessage", true)
    }

}