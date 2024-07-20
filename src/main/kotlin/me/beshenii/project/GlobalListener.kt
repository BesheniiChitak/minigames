package me.beshenii.project

import me.beshenii.project.util.key
import me.beshenii.project.util.queue_exit
import me.beshenii.project.util.queue_join
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType

object GlobalListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {

    }

    @EventHandler
    fun onPlayerCommandSend(event: PlayerCommandSendEvent) {
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val container = item.itemMeta.persistentDataContainer
        if (container[key("queue"), PersistentDataType.STRING] != null) {
            event.isCancelled = true
        } else {
            if (event.whoClicked.world.name == "world") {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val container = item.itemMeta.persistentDataContainer
        val key = container[key("queue"), PersistentDataType.STRING]
        if (key != null) {
            event.isCancelled = true
            when (key) {
                "join" -> {
                    event.player.inventory.setItem(4, queue_exit)
                    event.player.setCooldown(Material.GRAY_DYE, 20)
                }
                "exit" -> {
                    event.player.inventory.setItem(4, queue_join)
                    event.player.setCooldown(Material.LIME_DYE, 20)
                }
            }
        } else {
            if (event.player.world.name == "world") {
                event.isCancelled = true
            }
        }
    }

}