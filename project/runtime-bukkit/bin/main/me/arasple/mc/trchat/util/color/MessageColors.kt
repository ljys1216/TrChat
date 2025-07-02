package me.arasple.mc.trchat.util.color

import me.arasple.mc.trchat.module.conf.file.Settings
import me.arasple.mc.trchat.util.TrChatLogger
import org.bukkit.command.CommandSender
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import java.util.regex.Pattern

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
        TrChatLogger.debug { "Processing message color for ${sender.name}: $string" }

        if (Settings.miniMessage) {
            if (Settings.legacyColor) {
                string = preprocessLegacyColors(string)
                TrChatLogger.debug { "Preprocessed legacy colors for MiniMessage: $string" }
            }
            // MiniMessage will handle all color parsing
            return string
        }

        // Legacy color processing
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

        val result = string.colorify()
        TrChatLogger.debug { "Final colored message: $result" }
        return result
    }

    private fun preprocessLegacyColors(text: String): String {
        var processedText = text
        val buffer = StringBuffer()

        // Pattern for &c, &l, etc.
        val legacyPattern = Pattern.compile("(?i)&([0-9A-FK-OR])")
        var matcher = legacyPattern.matcher(processedText)
        while (matcher.find()) {
            val replacement = when (matcher.group(1).lowercase()) {
                "c" -> "<red>"
                "a" -> "<green>"
                "e" -> "<yellow>"
                "b" -> "<aqua>"
                "d" -> "<light_purple>"
                "f" -> "<white>"
                "0" -> "<black>"
                "1" -> "<dark_blue>"
                "2" -> "<dark_green>"
                "3" -> "<dark_aqua>"
                "4" -> "<dark_red>"
                "5" -> "<dark_purple>"
                "6" -> "<gold>"
                "7" -> "<gray>"
                "8" -> "<dark_gray>"
                "9" -> "<blue>"
                "l" -> "<bold>"
                "o" -> "<italic>"
                "n" -> "<underline>"
                "m" -> "<strikethrough>"
                "k" -> "<obfuscated>"
                "r" -> "<reset>"
                else -> null
            }
            if (replacement != null) {
                matcher.appendReplacement(buffer, replacement)
                TrChatLogger.debug { "Replaced legacy color '&${matcher.group(1)}' with '$replacement'" }
            }
        }
        matcher.appendTail(buffer)
        processedText = buffer.toString()
        buffer.setLength(0)

        // Pattern for &#RRGGBB
        val hexPattern = Pattern.compile("&#([0-9A-Fa-f]{6})")
        matcher = hexPattern.matcher(processedText)
        while (matcher.find()) {
            val hexCode = matcher.group(1)
            val replacement = "<#$hexCode>"
            matcher.appendReplacement(buffer, replacement)
            TrChatLogger.debug { "Replaced legacy hex color '&#$hexCode' with '$replacement'" }
        }
        matcher.appendTail(buffer)
        processedText = buffer.toString()
        buffer.setLength(0)

        // Pattern for &x&R&R&G&G&B&B
        val spigotHexPattern = Pattern.compile("(?i)&x(&[0-9A-F]){6}")
        matcher = spigotHexPattern.matcher(processedText)
        while (matcher.find()) {
            val hexCode = matcher.group().replace(Regex("(?i)&x|&"), "")
            val replacement = "<#$hexCode>"
            matcher.appendReplacement(buffer, replacement)
            TrChatLogger.debug { "Replaced legacy spigot hex color '${matcher.group()}' with '$replacement'" }
        }
        matcher.appendTail(buffer)
        processedText = buffer.toString()

        return processedText
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