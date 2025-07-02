package me.arasple.mc.trchat.module.internal.hook

import me.arasple.mc.trchat.module.internal.hook.impl.HookDiscordSRV
import me.arasple.mc.trchat.module.internal.hook.impl.HookEcoEnchants
import me.arasple.mc.trchat.module.internal.hook.impl.HookItemsAdder
import me.arasple.mc.trchat.module.internal.hook.impl.HookNova
import me.arasple.mc.trchat.module.internal.hook.type.HookDisplayItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.function.BiFunction

/**
 * @author Arasple
 * @date 2021/1/26 22:04
 */
@PlatformSide(Platform.BUKKIT)
object HookPlugin {

    val registry = arrayListOf(
        HookDiscordSRV(),
        HookEcoEnchants(),
        HookItemsAdder(),
        HookNova()
    )

    fun printInfo() {
        registry.filter { it.isHooked }.forEach {
            it.init()
            console().sendLang("Plugin-Dependency-Hooked", it.name)
        }
    }

    fun addHook(element: HookAbstract) {
        registry.add(element)
        console().sendLang("Plugin-Dependency-Hooked", element.name)
    }

    fun registerDisplayItemHook(name: String, func: BiFunction<ItemStack, Player, ItemStack>) {
        addHook(object : HookDisplayItem() {
            override fun getPluginName(): String {
                return name
            }
            override fun displayItem(item: ItemStack, player: Player): ItemStack {
                return func.apply(item, player)
            }
        })
    }

    fun getDiscordSRV(): HookDiscordSRV {
        return registry[0] as HookDiscordSRV
    }

    fun getEcoEnchants(): HookEcoEnchants {
        return registry[1] as HookEcoEnchants
    }

    fun getItemsAdder(): HookItemsAdder {
        return registry[2] as HookItemsAdder
    }

    fun getNova(): HookNova {
        return registry[3] as HookNova
    }

}