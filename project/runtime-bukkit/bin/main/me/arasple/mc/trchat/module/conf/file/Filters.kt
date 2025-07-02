package me.arasple.mc.trchat.module.conf.file

import me.arasple.mc.trchat.TrChat
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

/**
 * @author ItsFlicker
 * @since 2022/2/4 13:04
 */
@PlatformSide(Platform.BUKKIT)
object Filters {

    @Config("filter.yml")
    lateinit var conf: Configuration
        private set

    @Awake(LifeCycle.ENABLE)
    fun init() {
        conf.onReload { reload() }
    }

    fun reload(notify: ProxyCommandSender = console()) {
        TrChat.api().getFilterManager().loadFilter(
            conf.getStringList("Local"),
            conf.getStringList("Ignored-Punctuations"),
            conf.getString("Replacement", "*")!![0],
            conf.getBoolean("Cloud-Thesaurus.Enabled"),
            conf.getStringList("Cloud-Thesaurus.Urls"),
            conf.getStringList("Cloud-Thesaurus.Ignored"),
            updateCloud = true,
            notify = notify
        )
    }
}