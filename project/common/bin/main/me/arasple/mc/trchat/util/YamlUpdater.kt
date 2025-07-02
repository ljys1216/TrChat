package me.arasple.mc.trchat.util

import taboolib.common.PrimitiveIO
import taboolib.common.PrimitiveSettings
import taboolib.common.platform.function.getDataFolder
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * module-starrysky
 * com.mcstarrysky.starrysky.config.YamlUpdater
 *
 * @author 米擦亮
 * @since 2023/9/6 20:50
 */
object YamlUpdater {

    fun update(path: String, skipNodes: Array<String> = emptyArray(), updateExists: Boolean = true) {
        // 读取 Jar 包内的对应配置文件
        val cache = Configuration.loadFromInputStream(javaClass.classLoader.getResourceAsStream(path) ?: error("resource not found: $path"))
        val file = File(getDataFolder(), path)
        if (!file.exists()) {
            return
        }
        val config = Configuration.loadFromFile(file)

        val updated = mutableListOf<String>()
        read(cache, config, skipNodes, updated, updateExists)
        if (updated.isNotEmpty()) {
            config.saveToFile(config.file)
        }

        if (PrimitiveSettings.IS_DEBUG_MODE) {
            PrimitiveIO.println("Auto updated configuration: $path, with ${updated.size} elements updated.")
            for (node in updated) {
                PrimitiveIO.println("|- $node")
            }
        }
    }

    private fun read(cache: ConfigurationSection, to: ConfigurationSection, skipNodes: Array<String>, updated: MutableList<String>, updateExists: Boolean) {
        var name = cache.name
        var c = cache
        while (c.parent != null) {
            name = "${c.parent!!.name}.$name"
            c = c.parent!!
        }
        if (name.isNotEmpty()) {
            name += '.'
        }
        // 遍历给定新版配置文件的所有配置项目
        for (key in cache.getKeys(false)) {
            // 白名单配置项不进行任何检查
            if (key in skipNodes) continue

            // 旧版没有, 添加
            if (!to.contains(key)) {
                updated += "$name$key (+)"
                to[key] = cache[key]
                continue
            }

            // 是否不更新已存在配置, 只补全缺失项
            if (!updateExists) continue

            // 好像 switch case 不能判断为空, 我基础没学好
            if (cache[key] == null) {
                updated += "$name$key (${to[key]} -> null)"
                to[key] = null
                continue
            }

            val read = cache[key]

            if (read is ConfigurationSection) {
                val write = to[key]
                // 根本不是配置选区, 那肯定要覆盖掉了, 没话说了
                if (write == null || write !is ConfigurationSection) {
                    updated += "$name$key (${to[key]} -> $read)"
                    to[key] = read
                    continue
                }
                read(read, write, skipNodes, updated, true)
            } else {
                if (read == to[key]) continue
                updated += "$name$key (${to[key]} -> $read)"
                to[key] = read
            }
        }
    }
}