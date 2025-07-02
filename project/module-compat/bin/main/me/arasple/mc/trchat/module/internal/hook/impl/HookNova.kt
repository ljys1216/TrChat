package me.arasple.mc.trchat.module.internal.hook.impl

import me.arasple.mc.trchat.module.internal.hook.type.HookDisplayItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.getLocale
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import xyz.xenondevs.nova.api.Nova

class HookNova : HookDisplayItem() {

    override fun displayItem(item: ItemStack, player: Player): ItemStack {
        if (!isHooked || item.isAir()) {
            return item
        }
        val novaMaterial = Nova.materialRegistry.getOrNull(item) ?: return item
        return buildItem(XMaterial.SHULKER_SHELL) {
            amount = item.amount
            name = "§f" + novaMaterial.getLocalizedName(adaptPlayer(player).getLocale().lowercase())
        }
    }
}