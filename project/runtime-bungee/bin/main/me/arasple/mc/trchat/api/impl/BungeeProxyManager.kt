package me.arasple.mc.trchat.api.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import me.arasple.mc.trchat.api.ProxyMessageManager
import me.arasple.mc.trchat.module.internal.TrChatBungee
import me.arasple.mc.trchat.util.print
import me.arasple.mc.trchat.util.proxy.buildMessage
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.server
import taboolib.common.util.unsafeLazy
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author ItsFlicker
 * @since 2022/6/18 19:21
 */
@PlatformSide(Platform.BUNGEE)
object BungeeProxyManager : ProxyMessageManager {

    init {
        PlatformFactory.registerAPI<ProxyMessageManager>(this)
        server<ProxyServer>().registerChannel(TrChatBungee.TRCHAT_CHANNEL)
    }

    override val executor: ExecutorService by unsafeLazy {
        val factory = ThreadFactoryBuilder().setNameFormat("TrChat PluginMessage Processing Thread #%d").build()
        Executors.newFixedThreadPool(8, factory)
    }

    override val allNames = mutableMapOf<Int, Map<String, String?>>()

    override fun sendMessage(recipient: Any, vararg args: String): Future<*> {
        if (recipient !is ServerInfo) {
            return CompletableFuture.completedFuture(false)
        }
        return executor.submit {
            try {
                for (bytes in buildMessage(*args)) {
                    recipient.sendData(TrChatBungee.TRCHAT_CHANNEL, bytes)
                }
            } catch (e: IOException) {
                e.print("Failed to send proxy trchat message!")
            }
        }
    }

    fun sendMessageToAll(vararg args: String, predicate: (ServerInfo) -> Boolean = { true }): Future<*> {
        val recipients = server<ProxyServer>().servers.filter { (_, v) -> v.players.isNotEmpty() && predicate(v) }
        return executor.submit {
            try {
                for (bytes in buildMessage(*args)) {
                    recipients.forEach { (_, v) ->
                        v.sendData(TrChatBungee.TRCHAT_CHANNEL, bytes)
                    }
                }
            } catch (e: IOException) {
                e.print("Failed to send proxy trchat message!")
            }
        }
    }

    @Schedule(async = true, period = 100L)
    override fun updateAllNames() {
        sendMessageToAll(
            "UpdateAllNames",
            allNames.values.joinToString(",") { it.keys.joinToString(",") },
            allNames.values.joinToString(",") { it.values.joinToString(",") }
        )
    }

}