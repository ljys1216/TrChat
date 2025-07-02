package me.arasple.mc.trchat.module.internal.script

import me.arasple.mc.trchat.module.internal.script.js.JavaScriptAgent
import me.arasple.mc.trchat.module.internal.script.kether.KetherHandler
import org.bukkit.entity.Player

/**
 * @author ItsFlicker
 * @since 2022/6/15 18:05
 */
@JvmInline
value class Reaction(private val script: List<String>) {

    fun eval(player: Player, vararg additions: Pair<String, Any>): Any? {
        return if (script.isEmpty()) null
        else eval(player, script, *additions)
    }

    companion object {

        fun eval(player: Player, script: List<String>, vararg additions: Pair<String, Any>): Any? {
            val (isJavaScript, js) = JavaScriptAgent.serialize(script[0])
            return if (isJavaScript) JavaScriptAgent.eval(player, js!!, *additions).get()
            else KetherHandler.eval(script, player, additions.toMap()).get()
        }
    }
}