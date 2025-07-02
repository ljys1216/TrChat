package me.arasple.mc.trchat.module.internal.command.main

import me.arasple.mc.trchat.api.impl.BukkitProxyManager
import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.module.internal.data.PlayerData
import me.arasple.mc.trchat.util.data
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.bool
import taboolib.common.platform.command.command
import taboolib.common.platform.command.suggest
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

/**
 * @author ItsFlicker
 * @since 2022/2/6 15:01
 */
@PlatformSide(Platform.BUKKIT)
object CommandIgnore {

    @Awake(LifeCycle.ENABLE)
    fun ignore() {
        if (Settings.conf.getStringList("Options.Disabled-Commands").contains("ignore")) return
        command("ignore", listOf("trignore"), permission = "trchat.command.ignore") {
            dynamic("player") {
                suggest {
                    BukkitProxyManager.getPlayerNames().keys.filter { it !in PlayerData.vanishing }
                }
                execute<Player> { sender, ctx, _ ->
                    val player = Bukkit.getOfflinePlayer(ctx["player"])
                    if (!player.hasPlayedBefore()) {
                        return@execute sender.sendLang("Command-Player-Not-Exist")
                    }
                    if (sender.data.switchIgnored(player.uniqueId)) {
                        sender.sendLang("Ignore-Ignored-Player", player.name!!)
                    } else {
                        sender.sendLang("Ignore-Cancel-Player", player.name!!)
                    }
                }
                bool(optional = true) {
                    execute<Player> { sender, ctx, _ ->
                        val player = Bukkit.getOfflinePlayer(ctx["player"])
                        if (!player.hasPlayedBefore()) {
                            return@execute sender.sendLang("Command-Player-Not-Exist")
                        }
                        if (ctx.bool("boolean")) {
                            sender.data.addIgnored(player.uniqueId)
                            sender.sendLang("Ignore-Ignored-Player", player.name!!)
                        } else {
                            sender.data.removeIgnored(player.uniqueId)
                            sender.sendLang("Ignore-Cancel-Player", player.name!!)
                        }
                    }
                }
            }
            incorrectSender { sender, _ ->
                sender.sendLang("Command-Not-Player")
            }
            incorrectCommand { _, _, _, _ ->
                createHelper()
            }
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun ignoreList() {
        if (Settings.conf.getStringList("Options.Disabled-Commands").contains("ignorelist")) return
        command("ignorelist", permission = "trchat.command.ignore") {
            execute<Player> { sender, _, _ ->
                sender.sendLang(
                    "Ignore-List",
                    sender.data.ignored.map { Bukkit.getOfflinePlayer(it).name ?: it }.joinToString(", ")
                )
            }
            incorrectSender { sender, _ ->
                sender.sendLang("Command-Not-Player")
            }
        }
    }
}