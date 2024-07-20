package me.beshenii.project

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.player.PlayerJoinEvent

object GlobalListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {

    }

    @EventHandler
    fun onPlayerCommandSend(event: PlayerCommandSendEvent) {
    }

}