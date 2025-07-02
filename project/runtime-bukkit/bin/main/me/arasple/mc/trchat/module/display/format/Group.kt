package me.arasple.mc.trchat.module.display.format

import me.arasple.mc.trchat.module.internal.script.Condition

/**
 * @author ItsFlicker
 * @since 2022/2/5 11:05
 */
class Group(
    val condition: Condition?,
    val priority: Int,
    val content: JsonComponent
)