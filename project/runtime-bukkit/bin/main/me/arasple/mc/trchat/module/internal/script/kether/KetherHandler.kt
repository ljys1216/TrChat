package me.arasple.mc.trchat.module.internal.script.kether

import org.bukkit.command.CommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

object KetherHandler {

    private val namespace = listOf("trchat")

    fun eval(source: String, sender: CommandSender, vars: Map<String, Any> = emptyMap()): CompletableFuture<Any?> {
        return try {
            KetherShell.eval(
                source,
                ScriptOptions.new {
                    namespace(namespace)
                    sender(adaptCommandSender(sender))
                    vars(vars)
                }
            )
        } catch (e: LocalizedException) {
            println("§c[TrChat] §8Unexpected exception while parsing kether script:")
            e.localizedMessage.split("\n").forEach {
                println("         §8$it")
            }
            CompletableFuture.completedFuture(null)
        }
    }

    fun eval(source: List<String>, sender: CommandSender, vars: Map<String, Any> = emptyMap()): CompletableFuture<Any?> {
        return eval(source.joinToString("\n"), sender, vars)
    }

    fun parseInline(source: String, sender: CommandSender, vars: Map<String, Any> = emptyMap()): String {
        if (source.contains("{{")) {
            try {
                return KetherFunction.parse(
                    source,
                    ScriptOptions.new {
                        namespace(namespace)
                        sender(adaptCommandSender(sender))
                        vars(vars)
                    }
                )
            } catch (e: LocalizedException) {
                println("§c[TrChat] §8Unexpected exception while parsing kether script:")
                e.localizedMessage.split("\n").forEach {
                    println("         §8$it")
                }
            }
        }
        return source
    }

}