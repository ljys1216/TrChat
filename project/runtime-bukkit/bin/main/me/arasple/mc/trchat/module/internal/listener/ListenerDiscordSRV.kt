package me.arasple.mc.trchat.module.internal.listener

import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreBroadcastEvent
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import me.arasple.mc.trchat.TrChat
import me.arasple.mc.trchat.module.display.format.MsgComponent
import me.arasple.mc.trchat.module.internal.hook.HookPlugin
import me.arasple.mc.trchat.util.pass
import me.arasple.mc.trchat.util.session
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.adaptPlayer

class ListenerDiscordSRV {

    @Subscribe
    fun onChatPreProcess(e: GameChatMessagePreProcessEvent) {
        val player = e.player
        val channel = player.session.lastChannel ?: return
        if (channel.settings.sendToDiscord) {
            val message = TrChat.api().getFilterManager().filter(e.message, adaptPlayer(player)).filtered
            val component = (channel.formats
                .firstOrNull { it.condition.pass(player) }?.msg
                ?.firstOrNull { it.condition.pass(player) }?.content as? MsgComponent)
                ?.createComponent(player, message, channel.settings.disabledFunctions)
                ?: return
            e.messageComponent = GsonComponentSerializer.gson().deserialize(component.toRawMessage())
            if (channel.settings.discordChannel.isNotEmpty()) {
                e.channel = channel.settings.discordChannel
            }
        } else {
            e.isCancelled = true
        }
    }

    @Subscribe
    fun onMessagePreBroadcast(e: DiscordGuildMessagePreBroadcastEvent) {
        e.recipients.removeIf {
            (it as? Player)?.session?.getChannel()?.settings?.receiveFromDiscord == false
        }
    }

    @PlatformSide(Platform.BUKKIT)
    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun register() {
            if (HookPlugin.getDiscordSRV().isHooked) {
                HookPlugin.getDiscordSRV().registerListener(ListenerDiscordSRV())
            }
        }
    }
}