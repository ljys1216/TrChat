package me.arasple.mc.trchat.api

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface ProxyMessageManager {

    val executor: ExecutorService

    val allNames: MutableMap<Int, Map<String, String?>>

    fun sendMessage(recipient: Any, vararg args: String): Future<*>

    fun updateAllNames()

}