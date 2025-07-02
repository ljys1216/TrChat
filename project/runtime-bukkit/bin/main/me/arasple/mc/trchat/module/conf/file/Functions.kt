package me.arasple.mc.trchat.module.conf.file

import me.arasple.mc.trchat.module.conf.Loader
import me.arasple.mc.trchat.module.conf.Property
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.console
import taboolib.common.util.ResettableLazy
import taboolib.common5.Baffle
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.ConfigNodeTransfer
import taboolib.module.configuration.Configuration
import java.util.concurrent.TimeUnit

/**
 * @author ItsFlicker
 * @since 2021/12/12 11:40
 */
@PlatformSide(Platform.BUKKIT)
object Functions {

    @Config("function.yml", autoReload = true)
    lateinit var conf: Configuration
        private set

    @Awake(LifeCycle.LOAD)
    fun init() {
        conf.onReload {
            Loader.loadFunctions(console())
            ResettableLazy.reset("functions")
        }
    }

    @ConfigNode("General.Command-Controller.List", "function.yml")
    val commandController = ConfigNodeTransfer<List<*>, Map<String, Command>> {
        associate { string ->
            val (cmd, property) = Property.from(string!!.toString())
            val mCmd = Bukkit.getCommandAliases().entries.firstOrNull { (_, value) ->
                value.any { it.equals(cmd.split(" ")[0], ignoreCase = true) }
            }
            val key = if (mCmd != null) mCmd.key + cmd.substringAfter(' ') else cmd
            val exact = property[Property.EXACT].toBoolean()
            val condition = property[Property.CONDITION]
            val baffle = property[Property.COOLDOWN]?.toFloat()?.let {
                Baffle.of((it * 1000).toLong(), TimeUnit.MILLISECONDS)
            }
            val relocate = property[Property.RELOCATE]?.split(';')
            key to Command(exact, condition, baffle, relocate)
        }
    }

    class Command(val exact: Boolean, val condition: String?, val baffle: Baffle?, val relocate: List<String>?)

}