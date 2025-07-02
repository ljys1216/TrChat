package me.arasple.mc.trchat.module.internal.command.main

import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.module.internal.TrChatBukkit
import me.arasple.mc.trchat.util.data
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.command
import taboolib.common.platform.command.suggest
import taboolib.common.platform.command.suggestUncheck
import taboolib.common5.util.parseMillis
import taboolib.expansion.createHelper
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang
import java.text.SimpleDateFormat

/**
 * CommandPrivateMessage
 * me.arasple.mc.trchat.module.internal.command
 *
 * @author ItsFlicker
 * @since 2021/7/21 10:40
 */
@PlatformSide(Platform.BUKKIT)
object CommandMute {

    val muteDateFormat = SimpleDateFormat()

    @Awake(LifeCycle.ENABLE)
    fun mute() {
        if (Settings.conf.getStringList("Options.Disabled-Commands").contains("mute")) return
        command("mute", listOf("trmute"), description = "Mute a player", permission = "trchat.command.mute") {
            dynamic("player") {
                suggest {
                    onlinePlayers.map { it.name }
                }
                execute<CommandSender> { sender, ctx, _ ->
                    mute(sender, ctx["player"], "999d", "null")
                }
                dynamic("time") {
                    suggestUncheck {
                        listOf("1h", "2d", "15m")
                    }
                    execute<CommandSender> { sender, ctx, _ ->
                        mute(sender, ctx["player"], ctx["time"], "null")
                    }
                    dynamic("reason") {
                        execute<CommandSender> { sender, ctx, _ ->
                            mute(sender, ctx["player"], ctx["time"], ctx["reason"])
                        }
                    }
                }
            }
            incorrectCommand { _, _, _, _ ->
                createHelper()
            }
        }
        command("unmute", listOf("trunmute"), description = "Unmute a player", permission = "trchat.command.unmute") {
            dynamic("player") {
                suggest {
                    onlinePlayers.map { it.name }
                }
                execute<CommandSender> { sender, ctx, _ ->
                    val player = Bukkit.getPlayer(ctx["player"])
                        ?: return@execute sender.sendLang("Command-Player-Not-Exist")
                    player.data.updateMuteTime(0)
                    sender.sendLang("Mute-Cancel-Muted-Player", player.name)
                    player.sendLang("General-Cancel-Muted")
                }
            }
        }
    }

    fun mute(sender: CommandSender, name: String, time: String, reason: String) {
        val player = Bukkit.getPlayer(name)
            ?: return sender.sendLang("Command-Player-Not-Exist")
        val data = player.data
        data.updateMuteTime(time.parseMillis())
        data.setMuteReason(reason)
        sender.sendLang("Mute-Muted-Player", player.name, time, reason)
        player.sendLang("General-Muted", muteDateFormat.format(data.muteTime), data.muteReason)
    }

    @Awake(LifeCycle.ENABLE)
    fun muteall() {
        if (Settings.conf.getStringList("Options.Disabled-Commands").contains("muteall")) return
        command("muteall", listOf("globalmute"), "Mute all players", permission = "trchat.command.muteall") {
            execute<CommandSender> { sender, _, _ ->
                TrChatBukkit.isGlobalMuting = !TrChatBukkit.isGlobalMuting
                if (TrChatBukkit.isGlobalMuting) {
                    sender.sendLang("Mute-Muted-All")
                } else {
                    sender.sendLang("Mute-Cancel-Muted-All")
                }
            }
        }
    }
}