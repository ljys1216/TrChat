package me.arasple.mc.trchat.api

import taboolib.module.chat.ComponentText

/**
 * @author ItsFlicker
 * @since 2022/6/18 15:16
 */
interface ComponentManager {

    /**
     * 发送玩家聊天Component
     *
     * @param receiver 接收者 (ProxyCommandSender / Platform CommandSender)
     * @param component 内容
     * @param sender 发送者 (ProxyCommandSender / Platform CommandSender / UUID)
     */
    fun sendComponent(receiver: Any, component: ComponentText, sender: Any? = null)

    /**
     * 过滤Component
     *
     * @param component 原内容
     * @param maxLength 最大长度 (负数为不验证)
     * @return 过滤后内容
     */
    fun filterComponent(component: ComponentText, maxLength: Int = -1): ComponentText

    /**
     * 合法化Component
     *
     * @param maxLength 最大长度 (负数为不验证)
     */
    fun validateComponent(component: ComponentText, maxLength: Int = -1): ComponentText

}