package me.arasple.mc.trchat.api.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

class TrChatItemShowEvent(
    val player: Player,
    var item: ItemStack,
    val isCompatibleMode: Boolean
) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

}