package me.arasple.mc.trchat.api

import me.arasple.mc.trchat.module.internal.filter.FilteredObject
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.console

interface FilterManager {

    /**
     * 加载聊天过滤器
     *
     * @param updateCloud 是否更新云端词库
     * @param notify      接受通知反馈
     */
    fun loadFilter(
        localWords: List<String>,
        punctuations: List<String>,
        replacement: Char,
        isCloudEnabled: Boolean,
        cloudUrls: List<String>,
        ignoredCloudWords: List<String>,
        updateCloud: Boolean = true,
        notify: ProxyCommandSender? = console()
    )

    /**
     * 加载云端词库
     */
    fun loadCloudThesaurus(notify: ProxyCommandSender? = console())

    /**
     * 根据玩家的权限 (trchat.bypass.filter)，过滤一个字符串
     *
     * @param string  待过滤字符串
     * @param player 玩家
     * @param execute 是否真的过滤
     * @return 过滤后的字符串
     */
    fun filter(string: String, player: ProxyPlayer? = null, execute: Boolean = true): FilteredObject

}