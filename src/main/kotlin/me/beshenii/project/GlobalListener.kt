package me.beshenii.project

import me.beshenii.project.util.other.plain
import org.bukkit.Bukkit
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
        Bukkit.getServer().sendMessage(plain(event.commands.toString()))
        event.commands.remove("/plugins")
    }
}