package me.beshenii.project

import me.beshenii.project.util.*
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
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
        val player = event.player
        val item = event.item ?: return
        val container = item.itemMeta.persistentDataContainer
        val key = container[key("queue"), PersistentDataType.STRING]
        if (key != null && event.action.isRightClick && event.hand == EquipmentSlot.HAND) {
            event.isCancelled = true
            if (player.getCooldown(Material.MUSIC_DISC_5) == 0) {
                player.setCooldown(Material.MUSIC_DISC_5, 10)
                when (key) {
                    "join" -> {
                        if (queue_players.size + 2 >= max_players) {
                            player.sendMessage("В очереди максимум игроков! ($max_players)")
                            return
                        }
                        player.inventory.setItem(4, queue_exit)
                        queue_players.add(event.player)
                    }

                    "exit" -> {
                        event.player.inventory.setItem(4, queue_join)
                        queue_players.remove(event.player)
                    }
                }
            }
        } else {
            if (event.player.world.name == "world") {
                event.isCancelled = true
            }
        }
    }
}