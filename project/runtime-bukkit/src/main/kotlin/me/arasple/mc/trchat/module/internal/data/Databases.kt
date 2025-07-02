package me.arasple.mc.trchat.module.internal.data

import me.arasple.mc.trchat.api.event.CustomDatabaseEvent
import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.util.TrChatLogger
import me.arasple.mc.trchat.util.print
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.disablePlugin
import taboolib.expansion.playerDatabase
import taboolib.expansion.setupPlayerDatabase

/**
 * @author ItsFlicker
 * @since 2021/9/11 13:29
 */
@PlatformSide(Platform.BUKKIT)
object Databases {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        try {
            val type = Settings.conf.getString("Database.Method")?.uppercase()
            if (type == "LOCAL" || type == "SQLITE" || type == null) {
                TrChatLogger.debug { "Using database type: LOCAL" }
                setupPlayerDatabase()
            } else if (type == "SQL" || type == "MYSQL") {
                TrChatLogger.debug { "Using database type: SQL" }
                setupPlayerDatabase(
                    Settings.conf.getConfigurationSection("Database.SQL")!!,
                    Settings.conf.getString("Database.SQL.table")!! + "_v2"
                )
            } else {
                TrChatLogger.debug { "Using custom database type: $type" }
                val event = CustomDatabaseEvent(type)
                event.call()
                playerDatabase = event.database ?: error("Unsupported database type: $type")
            }
            TrChatLogger.info("Database loaded successfully.")
        } catch (t: Throwable) {
            t.print("Failed to load database! Plugin will be disabled.")
            disablePlugin()
        }
    }

}