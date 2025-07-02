package me.arasple.mc.trchat.api

import taboolib.common.platform.ProxyCommandSender

/**
 * @author ItsFlicker
 * @since 2022/6/19 19:55
 */
interface ChannelManager {

    /**
     * 加载所有聊天频道
     *
     * @param sender lang消息接收者
     */
    fun loadChannels(sender: ProxyCommandSender)

    /**
     * 获取聊天频道
     *
     * Bukkit -> Channel
     */
    fun getChannel(id: String): Any?

}