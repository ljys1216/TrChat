package me.arasple.mc.trchat.module.internal.logger

import org.slf4j.LoggerFactory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.submit
import java.io.PrintStream

/**
 * https://github.com/8aka-Team/Invero/blob/main/src/main/kotlin/cc/trixey/invero/common/logger/SLF4JLoggerSuppressor.kt
 * SLF4J日志抑制器
 * 用于屏蔽SLF4J的初始化警告日志
 */
@PlatformSide(Platform.BUKKIT)
object SLF4JLoggerSuppressor {

    @Awake(LifeCycle.ENABLE)
    fun setupLoggerSuppressor() {
        // 保存原始的System.err输出流
        val originalErr = System.err

        submit {
            // 恢复原始的错误输出流
            System.setErr(originalErr)
        }

        try {
            // 设置一个临时的错误输出流，过滤掉SLF4J相关的初始化警告
            System.setErr(object : PrintStream(originalErr) {
                override fun println(message: String?) {
                    if (!isSLF4JInitMessage(message)) {
                        super.println(message)
                    }
                }

                override fun print(message: String?) {
                    if (!isSLF4JInitMessage(message)) {
                        super.print(message)
                    }
                }
            })

            // 触发SLF4J的初始化
            LoggerFactory.getLogger(SLF4JLoggerSuppressor::class.java)

        } catch (_: Exception) {
        }
    }

    /**
     * 判断是否为SLF4J的初始化消息
     */
    private fun isSLF4JInitMessage(message: String?): Boolean {
        if (message == null) return false
        return message.contains("SLF4J:") && (
                message.contains("No SLF4J providers were found") ||
                        message.contains("Defaulting to no-operation (NOP) logger") ||
                        message.contains("See https://www.slf4j.org/codes.html") ||
                        message.contains("StaticLoggerBinder") ||
                        message.contains("slf4j-api") ||
                        message.contains("slf4j-simple") ||
                        message.contains("SLF4J initialization")
                )
    }
}