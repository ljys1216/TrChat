package me.arasple.mc.trchat.test

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import org.mockito.Mockito
import taboolib.platform.BukkitPlugin
import java.util.*

object MockTrChat {

    fun mockServer(): Server {
        val server = Mockito.mock(Server::class.java)
        Mockito.`when`(server.isPrimaryThread).thenReturn(true)
        Bukkit.setServer(server)
        return server
    }

    fun mockPlayer(name: String): Player {
        val player = Mockito.mock(Player::class.java)
        val attachment = Mockito.mock(PermissionAttachment::class.java)
        Mockito.`when`(player.name).thenReturn(name)
        Mockito.`when`(player.uniqueId).thenReturn(UUID.randomUUID())
        Mockito.`when`(player.addAttachment(Mockito.any(BukkitPlugin::class.java))).thenReturn(attachment)
        Mockito.`when`(player.hasPermission(Mockito.anyString())).thenAnswer {
            val permission = it.arguments[0] as String
            attachment.permissions.getOrDefault(permission, false)
        }
        return player
    }

    fun mockPlugin() {
        val plugin = Mockito.mock(BukkitPlugin::class.java)
        Mockito.`when`(BukkitPlugin.getInstance()).thenReturn(plugin)
    }

}