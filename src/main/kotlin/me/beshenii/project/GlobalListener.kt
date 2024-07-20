package me.beshenii.project

import me.beshenii.project.util.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

object GlobalListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.inventory.clear()
        player.teleport(Bukkit.getWorld("world")!!.spawnLocation)
        player.gameMode = GameMode.ADVENTURE

        if (cur_status == "queue") {
            player.inventory.setItem(4, queue_join)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player in queue_players) queue_players.remove(player)
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
        val size = queue_players.size
        if (key != null && event.action.isRightClick && event.hand == EquipmentSlot.HAND) {
            event.isCancelled = true
            if (player.getCooldown(Material.MUSIC_DISC_5) == 0) {
                player.setCooldown(Material.MUSIC_DISC_5, 10)
                when (key) {
                    "join" -> {
                        if (size >= max_players) {
                            player.sendMessage("В очереди максимум игроков! ($size)")
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