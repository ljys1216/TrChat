package me.arasple.mc.trchat.util

import me.arasple.mc.trchat.TrChat
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.pluginId
import taboolib.module.lang.sendLang

/**
 * @author Arasple
 * @date 2021/8/28 14:59
 */
object TrChatLogger {

    fun info(vararg message: Any?) {
        message.forEach {
            console().sendMessage("§f[TrChat] §7" + it.toString())
        }
    }

    fun warn(vararg message: Any?) {
        message.forEach {
            console().sendMessage("§f[TrChat] §e" + it.toString())
        }
    }

    fun error(vararg message: Any?) {
        message.forEach {
            console().sendMessage("§f[TrChat] §c" + it.toString())
        }
    }

    fun debug(message: () -> Any?) {
        if (TrChat.isDebug) {
            info("[Debug] " + message.invoke().toString())
        }
    }

    fun verbose(vararg message: Any?) {
        if (isPrimaryThread) {
            console().sendLang("Plugin-Verbose", pluginId, message.joinToString(", "))
        }
    }

}