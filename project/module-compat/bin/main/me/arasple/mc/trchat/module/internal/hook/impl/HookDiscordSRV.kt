package me.arasple.mc.trchat.module.internal.hook.impl

import github.scarsz.discordsrv.DiscordSRV
import me.arasple.mc.trchat.module.internal.hook.HookAbstract

class HookDiscordSRV : HookAbstract() {

    fun registerListener(listener: Any) {
        if (!isHooked) return
        DiscordSRV.api.subscribe(listener)
    }

}