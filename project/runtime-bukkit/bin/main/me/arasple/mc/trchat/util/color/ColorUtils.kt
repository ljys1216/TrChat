package me.arasple.mc.trchat.util.color

import org.bukkit.command.CommandSender

/**
 * @author Arasple
 * @date 2021/6/22 20:51
 */
fun String.colorize(sender: CommandSender): String {
    return MessageColors.replaceWithPermission(sender, this, MessageColors.Type.DEFAULT)
}