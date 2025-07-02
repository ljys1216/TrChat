package me.arasple.mc.trchat.module.internal.command.main

import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.module.display.channel.Channel
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.command
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * @author ItsFlicker
 * @since 2021/7/21 11:24
 */
@PlatformSide(Platform.BUKKIT)
object CommandChannel {

    @Awake(LifeCycle.ENABLE)
    fun register() {
        if (Settings.conf.getStringList("Options.Disabled-Commands").contains("channel")) return
        command("channel", listOf("chatchannel", "trchannel"), "TrChat Channel", permission = "trchat.command.channel") {
            literal("join") {
                dynamic("channel") {
                    suggestion<Player> { _, _ ->
                        Channel.channels.keys.toList()
                    }
                    execute<Player> { sender, _, argument ->
                        Channel.join(sender, argument)
                    }
                }
            }
            literal("quit", "leave") {
                execute<Player> { sender, _, _ ->
                    Channel.quit(sender, true)
                }
            }
            execute<Player> { _, _, _ ->
                createHelper()
            }
            incorrectSender { sender, _ ->
                sender.sendLang("Command-Not-Player")
            }
            incorrectCommand { _, _, _, _ ->
                createHelper()
            }
        }
    }
}