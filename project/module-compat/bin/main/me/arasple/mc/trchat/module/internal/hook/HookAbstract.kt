package me.arasple.mc.trchat.module.internal.hook

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import taboolib.common.util.unsafeLazy

/**
 * @author Arasple
 * @date 2021/1/26 22:02
 */
abstract class HookAbstract {

    open val name by unsafeLazy { getPluginName() }

    val plugin: Plugin? by unsafeLazy {
        Bukkit.getPluginManager().getPlugin(name)
    }

    val isHooked by unsafeLazy {
        plugin != null && plugin!!.isEnabled
    }

    open fun getPluginName(): String {
        return javaClass.simpleName.substring(4)
    }

    open fun init() = Unit

}