package me.arasple.mc.trchat.module.internal.hook.type

import me.arasple.mc.trchat.module.internal.hook.HookAbstract
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * TrChat
 * me.arasple.mc.trchat.module.internal.hook.type.HookDisplayItem
 *
 * @author xiaomu
 * @since 2022/10/24 8:22 PM
 */
abstract class HookDisplayItem : HookAbstract() {

    abstract fun displayItem(item: ItemStack, player: Player): ItemStack
}