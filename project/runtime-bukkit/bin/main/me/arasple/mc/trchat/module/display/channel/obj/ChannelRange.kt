package me.arasple.mc.trchat.module.display.channel.obj

/**
 * @author ItsFlicker
 * @since 2022/2/6 11:13
 */
class ChannelRange(val type: Type, val distance: Int) {

    enum class Type { ALL, SINGLE_WORLD, DISTANCE, SELF }
}