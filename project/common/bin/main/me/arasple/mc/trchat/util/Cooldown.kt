package me.arasple.mc.trchat.util

import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Cooldowns {

    private val COOLDOWNS = ConcurrentHashMap<UUID, Cooldown>()

    fun getCooldownLeft(uuid: UUID, type: String): Long {
        return COOLDOWNS.computeIfAbsent(uuid) { Cooldown() }.data.getOrDefault(type, 0L) - System.currentTimeMillis()
    }

    fun isInCooldown(uuid: UUID, type: String): Boolean {
        return getCooldownLeft(uuid, type) > 0
    }

    fun updateCooldown(uuid: UUID, type: String, lasts: Long) {
        COOLDOWNS.computeIfAbsent(uuid) { Cooldown() }.data[type] = System.currentTimeMillis() + lasts
    }
}

data class Cooldown(val data: ConcurrentHashMap<String, Long> = ConcurrentHashMap())

enum class CooldownType(val alias: String) {

    /**
     * Chat Cooldown Types
     */

    CHAT("Chat"),
    ITEM_SHOW("ItemShow"),
    MENTION("Mention"),
    MENTION_ALL("MentionAll"),
    INVENTORY_SHOW("InventoryShow"),
    ENDERCHEST_SHOW("EnderChestShow"),
    IMAGE_SHOW("ImageShow")

}