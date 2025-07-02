package me.arasple.mc.trchat.api

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface ClientMessageManager {

    var port: Int

    val executor: ExecutorService

    val mode: ProxyMode

    fun close() = Unit

    fun getPlayerNames(): Map<String, String?>

    fun getExactName(name: String): String?

    fun isPlayerOnline(name: String): Boolean

    fun sendMessage(recipient: Any?, data: Array<String>): Future<*>

}