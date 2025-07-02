package me.arasple.mc.trchat.module.display.channel

import me.arasple.mc.trchat.api.event.TrChatEvent
import me.arasple.mc.trchat.api.impl.BukkitProxyManager
import me.arasple.mc.trchat.module.display.channel.obj.ChannelBindings
import me.arasple.mc.trchat.module.display.channel.obj.ChannelEvents
import me.arasple.mc.trchat.module.display.channel.obj.ChannelExecuteResult
import me.arasple.mc.trchat.module.display.channel.obj.ChannelSettings
import me.arasple.mc.trchat.module.display.format.Format
import me.arasple.mc.trchat.module.display.format.MsgComponent
import me.arasple.mc.trchat.module.internal.command.main.CommandReply
import me.arasple.mc.trchat.module.internal.data.ChatLogs
import me.arasple.mc.trchat.module.internal.data.PlayerData
import me.arasple.mc.trchat.module.internal.service.Metrics
import me.arasple.mc.trchat.util.pass
import me.arasple.mc.trchat.util.sendComponent
import me.arasple.mc.trchat.util.session
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.command.suggest
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.util.subList
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.lang.sendLang
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang

/**
 * @author ItsFlicker
 * @since 2022/2/8 11:03
 */
class PrivateChannel(
    id: String,
    settings: ChannelSettings,
    bindings: ChannelBindings,
    events: ChannelEvents,
    val sender: List<Format>,
    val receiver: List<Format>,
    consoleFormat: List<Format>
) : Channel(id, settings, bindings, events, emptyList(), consoleFormat) {

    var consolePrivateTo: String? = null

    override fun init() {
        registerCommand()
        onlinePlayers.filter { it.session.channel == id }.forEach {
            join(it, this, hint = false)
        }
    }

    override fun registerCommand() {
        if (bindings.command.isNullOrEmpty()) return
        command(
            name = bindings.command[0],
            aliases = subList(bindings.command, 1),
            description = "TrChat channel $id",
            permission = "trchat.command.channel.${id.lowercase()}",
            permissionDefault = PermissionDefault.TRUE
        ) {
            execute<Player> { sender, _, _ ->
                if (sender.session.channel == this@PrivateChannel.id) {
                    quit(sender, true)
                } else {
                    sender.sendLang("Private-Message-No-Player")
                }
            }
            dynamic("player", optional = true) {
                suggest {
                    BukkitProxyManager.getPlayerNamesMerged().filter { it !in PlayerData.vanishing }
                }
                execute<Player> { sender, _, argument ->
                    sender.session.lastPrivateTo = BukkitProxyManager.getExactName(argument)
                        ?: return@execute sender.sendLang("Command-Player-Not-Exist")
                    join(sender, this@PrivateChannel)
                }
                dynamic("message", optional = true) {
                    execute<CommandSender> { sender, ctx, argument ->
                        BukkitProxyManager.getExactName(ctx["player"])?.let {
                            if (sender is Player) sender.session.lastPrivateTo = it
                            else consolePrivateTo = it
                            execute(sender, argument)
                        } ?: sender.sendLang("Command-Player-Not-Exist")
                    }
                }
            }
            incorrectSender { sender, _ ->
                sender.sendLang("Command-Not-Player")
            }
        }
    }

    override fun execute(sender: CommandSender, message: String): ChannelExecuteResult {
        if (sender is Player) {
            return execute(sender, message)
        }
        val to = consolePrivateTo ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_RECEIVER)
        val component = Components.empty()
        val toPlayer = Bukkit.getPlayer(to)
        if (toPlayer?.isOnline == true) {
            consoleFormat.firstOrNull { it.condition.pass(toPlayer) }?.let { format ->
                format.prefix
                    .mapNotNull { prefix -> prefix.value.firstOrNull { it.condition.pass(toPlayer) }?.content?.toTextComponent(toPlayer) }
                    .forEach { prefix -> component.append(prefix) }
                format.msg.firstOrNull { it.condition.pass(toPlayer) }
                    ?.let { component.append((it.content as MsgComponent).createComponent(toPlayer, message, settings.disabledFunctions)) }
                    ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)
                format.suffix
                    .mapNotNull { suffix -> suffix.value.firstOrNull { it.condition.pass(toPlayer) }?.content?.toTextComponent(toPlayer) }
                    .forEach { suffix -> component.append(suffix) }
            } ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)
        } else {
            consoleFormat.firstOrNull()?.let { format ->
                format.prefix.forEach { prefix ->
                    component.append(prefix.value[0].content.toTextComponent(sender)) }
                component.append((format.msg[0].content as MsgComponent).createComponent(sender, message, settings.disabledFunctions))
                format.suffix.forEach { suffix ->
                    component.append(suffix.value[0].content.toTextComponent(sender)) }
            } ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)
        }

        console().sendComponent(null, component)
        if (settings.proxy && BukkitProxyManager.processor != null) {
            BukkitProxyManager.sendPrivateRaw(
                onlinePlayers.firstOrNull(),
                to,
                "CONSOLE",
                component
            )
            BukkitProxyManager.sendProxyLang(onlinePlayers.firstOrNull(), to, "Private-Message-Receive", "CONSOLE")
        } else {
            getProxyPlayer(to)?.let {
                it.sendComponent(null, component)
                it.sendLang("Private-Message-Receive", "CONSOLE")
            }
        }
        return ChannelExecuteResult.success(component, component)
    }

    override fun execute(player: Player, message: ComponentText, toConsole: Boolean): ChannelExecuteResult {
        var plain = message.toPlainText()
        if (!checkLimits(player, plain)) {
            return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.LIMITED)
        }
        val session = player.session
        val to = session.lastPrivateTo
        if (!BukkitProxyManager.isPlayerOnline(to)) {
            player.sendLang("Command-Player-Not-Exist")
            quit(player, true)
            return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_RECEIVER)
        }
        session.lastChannel = this
        session.lastPrivateMessage = plain
        val event = TrChatEvent(this, session, message)
        if (!event.call()) {
            return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.EVENT)
        }
        val msg = events.process(player, event.component)
            ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.EVENT)
        plain = msg.toPlainText()

        val send = Components.empty()
        val msgComponent = Components.empty()
        sender.firstOrNull { it.condition.pass(player) }?.let { format ->
            format.prefix
                .mapNotNull { prefix -> prefix.value.firstOrNull { it.condition.pass(player) }?.content?.toTextComponent(player) }
                .forEach { prefix -> send.append(prefix) }
            format.msg.firstOrNull { it.condition.pass(player) }
                ?.let { msgComponent.append((it.content as MsgComponent).createComponent(player, msg, settings.disabledFunctions)) }
                ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)
            send.append(msgComponent)
            format.suffix
                .mapNotNull { suffix -> suffix.value.firstOrNull { it.condition.pass(player) }?.content?.toTextComponent(player) }
                .forEach { suffix -> send.append(suffix) }
        } ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)

        val receive = Components.empty()
        receiver.firstOrNull { it.condition.pass(player) }?.let { format ->
            format.prefix
                .mapNotNull { prefix -> prefix.value.firstOrNull { it.condition.pass(player) }?.content?.toTextComponent(player) }
                .forEach { prefix -> receive.append(prefix) }
            receive.append(msgComponent)
            format.suffix
                .mapNotNull { suffix -> suffix.value.firstOrNull { it.condition.pass(player) }?.content?.toTextComponent(player) }
                .forEach { suffix -> receive.append(suffix) }
        } ?: return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.NO_FORMAT)

        if (session.cancelChat) {
            session.cancelChat = false
            return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.EVENT)
        }
        // Channel event
        if (!events.send(player, to, plain)) {
            return ChannelExecuteResult(failedReason = ChannelExecuteResult.FailReason.EVENT)
        }
        player.sendComponent(player, send)

        PlayerData.spying.forEach {
            Bukkit.getPlayer(it)
                ?.sendLang("Private-Message-Spy-Format", player.name, to, msgComponent.toLegacyText())
        }
        console().sendLang("Private-Message-Spy-Format", player.name, to, msgComponent.toLegacyText())

        CommandReply.lastMessageFrom[to] = player.name
        ChatLogs.logPrivate(player.name, to, plain)
        Metrics.increase(0)

        if (settings.proxy && BukkitProxyManager.processor != null) {
            BukkitProxyManager.sendPrivateRaw(
                player,
                to,
                player.name,
                receive
            )
            BukkitProxyManager.sendProxyLang(player, to, "Private-Message-Receive", player.name)
        } else {
            getProxyPlayer(to)?.let {
                it.sendComponent(player, receive)
                it.sendLang("Private-Message-Receive", player.name)
            }
        }
        return ChannelExecuteResult.success(send, receive)
    }
}