package me.arasple.mc.trchat.module.display.channel.obj

import taboolib.module.chat.ComponentText

class ChannelExecuteResult(
    val senderComponent: ComponentText? = null,
    val receiverComponent: ComponentText? = null,
    val failedReason: FailReason? = null
) {

    enum class FailReason {

        LIMITED, NO_RECEIVER, NO_FORMAT, EVENT, EXCEPTION
    }

    companion object {

        fun success(senderComponent: ComponentText, receiverComponent: ComponentText? = null): ChannelExecuteResult {
            return ChannelExecuteResult(senderComponent, receiverComponent)
        }
    }
}