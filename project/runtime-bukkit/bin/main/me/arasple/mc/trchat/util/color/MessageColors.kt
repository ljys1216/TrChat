package me.arasple.mc.trchat.util.color

import org.bukkit.command.CommandSender
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide

/**
 * @author Arasple
 * @date 2019/8/15 20:52
 */
@PlatformSide(Platform.BUKKIT)
object MessageColors {

    const val COLOR_PERMISSION_NODE = "trchat.color."
    const val FORCE_CHAT_COLOR_PERMISSION_NODE = "trchat.color.force-defaultcolor."

    private val specialColors = arrayOf(
        "simple",
        "rainbow",
        "gradients",
        "hex",
        "anvil",
        "sign",
        "book"
    )

    @JvmOverloads
    fun replaceWithPermission(sender: CommandSender, strings: List<String>, type: Type = Type.DEFAULT): List<String> {
        return strings.map { replaceWithPermission(sender, it, type) }
    }

    @JvmOverloads
    fun replaceWithPermission(sender: CommandSender, s: String, type: Type = Type.DEFAULT): String {
        var string = s

        if (type == Type.ANVIL && sender.hasPermission("trchat.color.anvil.*")) {
            return string.colorify()
        }
        if (type == Type.SIGN && sender.hasPermission("trchat.color.sign.*")) {
            return string.colorify()
        }
        if (type == Type.BOOK && sender.hasPermission("trchat.color.book.*")) {
            return string.colorify()
        }

        if (sender.hasPermission("$COLOR_PERMISSION_NODE*")) {
            return string.colorify()
        }

        // 2025/6/18 https://github.com/TrPlugins/TrChat/issues/477
        if (sender.hasPermission(COLOR_PERMISSION_NODE + "rainbow")) {
            string = string.parseRainbow()
        }

        if (sender.hasPermission(COLOR_PERMISSION_NODE + "gradients")) {
            string = string.parseGradients()
        }

        if (sender.hasPermission(COLOR_PERMISSION_NODE + "hex")) {
            string = string.parseHex()
        }

        getColors(sender).forEach { color ->
            string = string.replace(color, CustomColor.get(color).color)
        }

        return string
    }

    private fun getColorsFromPermissions(sender: CommandSender, prefix: String): List<String> {
        sender.recalculatePermissions()
        return sender.effectivePermissions.mapNotNull {
            val permission = it.permission
            if (permission.startsWith(prefix)) {
                permission.removePrefix(prefix).let { color -> if (color.length == 1) "&$color" else color }
            } else {
                null
            }
        }.filterNot { it in specialColors }
    }

    fun getColors(sender: CommandSender): List<String> {
        return getColorsFromPermissions(sender, COLOR_PERMISSION_NODE)
    }

    fun getForceColors(sender: CommandSender): List<String> {
        return getColorsFromPermissions(sender, FORCE_CHAT_COLOR_PERMISSION_NODE)
    }

    enum class Type {

        DEFAULT, ANVIL, SIGN, BOOK
    }
}