package me.arasple.mc.trchat.api.impl

import com.google.gson.JsonObject
import me.arasple.mc.trchat.api.FilterManager
import me.arasple.mc.trchat.module.internal.filter.Filter
import me.arasple.mc.trchat.module.internal.filter.FilteredObject
import me.arasple.mc.trchat.module.internal.service.Metrics
import me.arasple.mc.trchat.util.parseJson
import me.arasple.mc.trchat.util.reportOnce
import taboolib.common.LifeCycle
import taboolib.common.PrimitiveIO
import taboolib.common.io.digest
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.decodeUnicode
import taboolib.module.lang.sendLang
import java.io.BufferedInputStream
import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets

object DefaultFilterManager : FilterManager {

    @Awake(LifeCycle.CONST)
    fun init() {
        PlatformFactory.registerAPI<FilterManager>(this)
    }

    var isCloudEnabled = true
    val cloud_url = mutableSetOf<String>()
    val ignored_cloud_words = mutableSetOf<String>()
    val custom_replacements = mutableMapOf<String, String>()
    val white_words = mutableListOf<String>()

    private val cloud_words = mutableSetOf<String>()
    private val cloud_last_update = mutableMapOf<String, String>()

    private var initialized = false

    override fun loadFilter(
        localWords: List<String>,
        punctuations: List<String>,
        replacement: Char,
        isCloudEnabled: Boolean,
        cloudUrls: List<String>,
        ignoredCloudWords: List<String>,
        updateCloud: Boolean,
        notify: ProxyCommandSender?
    ) {
        // 初始化本地配置
        Filter.setSensitiveWord(localWords)
        Filter.setPunctuations(punctuations)
        Filter.setReplacement(replacement)

        DefaultFilterManager.isCloudEnabled = isCloudEnabled
        cloud_url += cloudUrls
        ignored_cloud_words += ignoredCloudWords

        notify?.sendLang("Plugin-Loaded-Filter-Local", localWords.size)

        if (!initialized) {
            submitAsync(period = (60 * 60 * 20).toLong()) {
                loadCloudThesaurus(notify)
            }
            initialized = true
        } else if (updateCloud) {
            submitAsync {
                loadCloudThesaurus(notify)
            }
        }
    }

    override fun loadCloudThesaurus(notify: ProxyCommandSender?) {
        if (!isCloudEnabled || cloud_url.isEmpty()) {
            return
        }
        val collected = mutableSetOf<String>()
        cloud_url.forEach {
            collected += catchCloudThesaurus(it, notify)
        }
        cloud_words += collected
        if (cloud_words.isEmpty()) {
            notify?.sendLang("Plugin-Failed-Load-Filter-Cloud")
        } else {
            Filter.addSensitiveWord(cloud_words.sortedByDescending { it.length })
        }
    }

    override fun filter(string: String, player: ProxyPlayer?, execute: Boolean): FilteredObject {
        return if (execute && player?.hasPermission("trchat.bypass.filter") != true) {
            Filter.doFilter(string).also {
                Metrics.increase(1, it.sensitiveWords)
            }
        } else {
            FilteredObject(string, 0)
        }
    }

    private fun catchCloudThesaurus(url: String, notify: ProxyCommandSender?): List<String> {
        return kotlin.runCatching {
            URL(url).openConnection().also { it.connectTimeout = 30 * 1000; it.readTimeout = 30 * 1000 }.getInputStream().use { inputStream ->
                BufferedInputStream(inputStream).use { bufferedInputStream ->
                    val origin = PrimitiveIO.readFully(bufferedInputStream, StandardCharsets.UTF_8)
                    val database = origin.parseJson().asJsonObject
                    require(database.has("lastUpdateDate") && database.has("words")) {
                        "Wrong database json object"
                    }
                    val collected = readDatabase(url, database)
                    if (collected.isNotEmpty()) {
                        val file = newFile(getDataFolder(), "filters/${url.digest("md5")}")
                        file.writeText(origin)
                        notify?.sendLang("Plugin-Loaded-Filter-Cloud", collected.size, url, cloud_last_update[url]!!)
                    }
                    collected
                }
            }
        }.getOrElse { t ->
            val file = File(getDataFolder(), "filters/${url.digest("md5")}")
            if (file.exists()) {
                t.reportOnce("Failed to catch cloud thesaurus of $url.Use cache instead.", printStackTrace = false)
                val database = file.readText().parseJson().asJsonObject
                readDatabase(url, database)
            } else {
                t.reportOnce("Failed to catch cloud thesaurus of $url.", printStackTrace = false)
                emptyList()
            }
        }
    }

    private fun readDatabase(url: String, database: JsonObject): List<String> {
        return try {
            val lastUpdateDate = database["lastUpdateDate"].asString
            cloud_last_update[url] = when (cloud_last_update[url]) {
                lastUpdateDate -> return emptyList()
                null -> lastUpdateDate
                else -> lastUpdateDate
            }
            database["words"].asJsonArray.mapNotNull {
                val word = it.asString.decodeUnicode()
                if (ignored_cloud_words.none { w -> w.equals(word, ignoreCase = true) }) {
                    word
                } else {
                    null
                }
            }
        } catch (t: Throwable) {
            t.reportOnce("Failed to load filter database of $url!")
            emptyList()
        }
    }

}
