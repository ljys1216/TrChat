package me.arasple.mc.trchat.module.internal.script

import me.arasple.mc.trchat.module.internal.script.js.JavaScriptAgent
import me.arasple.mc.trchat.module.internal.script.kether.KetherHandler
import org.bukkit.entity.Player
import taboolib.common5.Coerce

/**
 * @author Arasple
 * @since 2021/8/29 15:16
 */
@JvmInline
value class Condition(private val script: String) {

    fun eval(player: Player): Boolean {
        return if (script.isEmpty()) true
        else eval(player, script)
    }

    fun isEmpty(): Boolean {
        return script.isEmpty()
    }

    companion object {

        val EMPTY = Condition("")

        fun eval(player: Player, script: String): Boolean {
            val (isJavaScript, js) = JavaScriptAgent.serialize(script)
            return if (isJavaScript) JavaScriptAgent.eval(player, js!!).thenApply { Coerce.toBoolean(it) }.get()
            else KetherHandler.eval(script, player).thenApply { Coerce.toBoolean(it) }.get()
        }
    }
}