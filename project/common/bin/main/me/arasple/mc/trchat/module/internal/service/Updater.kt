package me.arasple.mc.trchat.module.internal.service

import me.arasple.mc.trchat.util.parseJson
import taboolib.common.LifeCycle
import taboolib.common.PrimitiveIO
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.common.util.Version
import taboolib.module.lang.sendLang
import java.io.BufferedInputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

@SkipTo(LifeCycle.LOAD)
object Updater {

    private const val api_url = "https://api.github.com/repos/TrPlugins/TrChat/releases/latest"
    private var notify = false
    val notified = mutableListOf<UUID>()
    val current_version = Version(pluginVersion)
    var latest_Version = Version("0.0")
    var information = ""

    fun grabInfo() {
        if (latest_Version.version[0] > 0) {
            return
        }
        kotlin.runCatching {
            URL(api_url).openConnection().also { it.connectTimeout = 30 * 1000; it.readTimeout = 30 * 1000 }.getInputStream().use { inputStream ->
                BufferedInputStream(inputStream).use { bufferedInputStream ->
                    val read = PrimitiveIO.readFully(bufferedInputStream, StandardCharsets.UTF_8)
                    val json = read.parseJson().asJsonObject
                    val latestVersion = json["tag_name"].asString.removePrefix("v")
                    latest_Version = Version(latestVersion)
                    information = json["body"].asString
                    notifyConsole()
                }
            }
        }
    }

    fun notifyPlayer(player: ProxyPlayer) {
        if (player.hasPermission("trchat.admin") && latest_Version > current_version && player.uniqueId !in notified) {
            player.sendLang("Plugin-Updater-Header", current_version.source, latest_Version.source)
            information.lines().forEach {
                player.sendMessage(it)
            }
            player.sendLang("Plugin-Updater-Footer")
            notified.add(player.uniqueId)
        }
    }

    private fun notifyConsole() {
        if (latest_Version > current_version) {
            console().sendLang("Plugin-Updater-Header", current_version.source, latest_Version.source)
            console().sendMessage(information)
            console().sendLang("Plugin-Updater-Footer")
        } else {
            if (!notify) {
                notify = true
                if (current_version > latest_Version) {
                    console().sendLang("Plugin-Updater-Dev")
                } else {
                    console().sendLang("Plugin-Updater-Latest")
                }
            }
        }
    }

}
