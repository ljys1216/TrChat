package me.arasple.mc.trchat.api

/**
 * @author ItsFlicker
 * @since 2022/6/18 15:13
 */
interface TrChatAPI {

    fun getComponentManager(): ComponentManager

    fun getChannelManager(): ChannelManager

    fun getFilterManager(): FilterManager

    fun getClientMessageManager(): ClientMessageManager

    fun getProxyMessageManager(): ProxyMessageManager

}