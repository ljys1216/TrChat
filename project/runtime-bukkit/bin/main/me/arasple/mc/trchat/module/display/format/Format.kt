package me.arasple.mc.trchat.module.display.format

import me.arasple.mc.trchat.module.internal.script.Condition

/**
 * @author ItsFlicker
 * @since 2021/12/11 23:27
 */
class Format(
    val condition: Condition?,
    val priority: Int,
    val prefix: Map<String, List<Group>>,
    val msg: List<Group>,
    val suffix: Map<String, List<Group>>
)