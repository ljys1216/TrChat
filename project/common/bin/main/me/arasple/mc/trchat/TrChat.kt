package me.arasple.mc.trchat

import me.arasple.mc.trchat.api.TrChatAPI

/**
 * @author ItsFlicker
 * @since 2022/6/18 14:50
 */
object TrChat {

    private var api: TrChatAPI? = null

    fun api(): TrChatAPI {
        return api ?: error("TrChat is loading or failed to load!")
    }

    fun register(api: TrChatAPI) {
        this.api = api
    }

}