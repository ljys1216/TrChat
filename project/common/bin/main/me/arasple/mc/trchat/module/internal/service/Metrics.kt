package me.arasple.mc.trchat.module.internal.service

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.runningPlatform
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.SingleLineChart

/**
 * @author Arasple
 */
object Metrics {

    private var metrics: Metrics? = null

    private val counts = intArrayOf(0, 0)

    @Awake(LifeCycle.ACTIVE)
    internal fun init() {
        metrics = when (runningPlatform) {
            Platform.BUKKIT -> Metrics(5802, pluginVersion, runningPlatform).apply {
                // 聊天次数统计
                addCustomChart(SingleLineChart("chat_counts") {
                    val i = counts[0]
                    counts[0] = 0
                    i
                })
                // 敏感词过滤器启用统计
                addCustomChart(SingleLineChart("filter_counts") {
                    val i = counts[1]
                    counts[1] = 0
                    i
                })
            }
            Platform.BUNGEE -> Metrics(5803, pluginVersion, runningPlatform)
            Platform.VELOCITY -> Metrics(12541, pluginVersion, runningPlatform)
            else -> null
        }
    }

    fun increase(index: Int, value: Int = 1) {
        if (counts[index] < Int.MAX_VALUE) {
            counts[index] += value
        }
    }

}