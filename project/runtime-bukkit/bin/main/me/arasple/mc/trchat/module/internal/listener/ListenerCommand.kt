package me.arasple.mc.trchat.module.internal.listener

import me.arasple.mc.trchat.module.conf.file.Functions
import me.arasple.mc.trchat.util.toCondition
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.replaceWithOrder
import taboolib.common.util.subList
import taboolib.platform.util.sendLang

/**
 * @author ItsFlicker
 * @date 2020/1/16 21:41
 */
@PlatformSide(Platform.BUKKIT)
object ListenerCommand {

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val player = e.player
        var cmd = e.message.removePrefix("/").trimIndent()

        if (!Functions.conf.getBoolean("General.Command-Controller.Enable", true) || cmd.isEmpty()) {
            return
        }
        val command = cmd.split(" ")

        val mCmd = Bukkit.getCommandAliases().entries.firstOrNull { (_, value) ->
            value.any { it.equals(command[0], ignoreCase = true) }
        }
        cmd = if (mCmd != null) mCmd.key + cmd.substringAfter(' ') else cmd

        val controller = Functions.commandController.get().entries.firstOrNull {
            (it.value.exact && cmd.equals(it.key, ignoreCase = true))
                    || (!it.value.exact && cmd.substringBefore(' ').equals(it.key.substringBefore(' '), ignoreCase = true))
        }?.value ?: return

        // 执行条件
        val condition = controller.condition?.replaceWithOrder(*subList(command, 1).toTypedArray())
        if (condition != null && !condition.toCondition().eval(player)) {
            e.isCancelled =  true
            player.sendLang("Command-Controller-Deny")
            return
        }

        // 执行冷却
        val baffle = controller.baffle
        if (baffle != null && !baffle.hasNext(player.name) && !player.hasPermission("trchat.bypass.cmdcooldown")) {
            e.isCancelled =  true
            player.sendLang("Command-Controller-Cooldown")
            return
        }

        // 执行跳转
        val relocate = controller.relocate?.map { it.replaceWithOrder(*subList(command, 1).toTypedArray()) }
        if (relocate != null) {
            e.isCancelled = true
            relocate.forEach {
                player.performCommand(it)
            }
            return
        }
    }
}