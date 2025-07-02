package me.arasple.mc.trchat.module.internal.proxy.redis

import me.arasple.mc.trchat.api.impl.BukkitProxyManager
import me.arasple.mc.trchat.module.conf.file.Settings
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.expansion.AlkaidRedis
import taboolib.expansion.SingleRedisConnection
import taboolib.expansion.SingleRedisConnector
import taboolib.expansion.fromConfig
import taboolib.module.configuration.ConfigNode

@PlatformSide(Platform.BUKKIT)
object RedisManager {

    private var connector: SingleRedisConnector? = null
    var connection: SingleRedisConnection? = null
    var channel = "trchat-message"

    @ConfigNode("Redis.enabled", "settings.yml")
    var enabled = false
        private set

    operator fun invoke(default: Boolean = true): SingleRedisConnection? {
        if (!enabled) {
            return null
        }
        if (connector == null) {
            connector = AlkaidRedis.create().apply {
                fromConfig(Settings.conf.getConfigurationSection("Redis")!!)
            }
        }
        connection?.close()
        connection = connector!!.connect().connection()
        if (default) {
            init(connection!!)
        }
        return connection
    }

    fun init(connection: SingleRedisConnection) {
        connection.subscribe(channel) {
            val message = get<TrRedisMessage>(ignoreConstructor = true)
            BukkitProxyManager.processor?.execute(message.data)
        }
    }

    fun sendMessage(message: TrRedisMessage) {
        if (enabled) {
            (connection ?: RedisManager())!!.publish(channel, message)
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun close() {
        connection?.close()
    }

}