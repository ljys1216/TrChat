package me.arasple.mc.trchat.api.impl

import me.arasple.mc.trchat.TrChat
import me.arasple.mc.trchat.api.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * @author ItsFlicker
 * @since 2022/6/18 15:26
 */
object DefaultTrChatAPI : TrChatAPI {

    @Awake(LifeCycle.CONST)
    fun init() {
        TrChat.register(this)
    }

    override fun getComponentManager() = PlatformFactory.getAPI<ComponentManager>()

    override fun getChannelManager() = PlatformFactory.getAPI<ChannelManager>()

    override fun getFilterManager() = PlatformFactory.getAPI<FilterManager>()

    override fun getClientMessageManager() = PlatformFactory.getAPI<ClientMessageManager>()

    override fun getProxyMessageManager() = PlatformFactory.getAPI<ProxyMessageManager>()

}