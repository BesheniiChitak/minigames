package me.beshenii.project

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import me.beshenii.project.util.*
import me.beshenii.project.util.other.reset
import me.beshenii.project.util.other.runTaskLater
import net.minecraft.world.entity.LightningBolt
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LightningStrike
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandSendEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

object GlobalListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.reset()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player in queue_players) queue_players.remove(player)
        if (player in game_players) game_players.remove(player)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player
        if (player in game_players) {
            game_players.remove(player)
            player.world.spawnEntity(player.location, EntityType.LIGHTNING)
        }
    }

    @EventHandler
    fun onPlayerPostRespawn(event: PlayerPostRespawnEvent) {
        runTaskLater(1) { event.player.reset() }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val container = item.itemMeta?.persistentDataContainer ?: return
        if (container[key("queue"), PersistentDataType.STRING] != null) {
            event.isCancelled = true
        } else {
            if (event.whoClicked.world.name == "world") {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val item = event.offHandItem ?: return
        val container = item.itemMeta?.persistentDataContainer ?: return
        if (container[key("queue"), PersistentDataType.STRING] != null) {
            event.isCancelled = true
        } else {
            if (event.player.world.name == "world") {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        val container = item.itemMeta?.persistentDataContainer ?: return
        if (container[key("queue"), PersistentDataType.STRING] != null) {
            event.isCancelled = true
        } else {
            if (event.player.world.name == "world") {
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

                    "spectate" -> {
                        player.gameMode = GameMode.SPECTATOR
                        val loc = Location(Bukkit.getWorld("game"), 0.0, 32.0, 0.0)
                        player.teleport(loc)
                    }
                }
            }
        } else {
            if (event.player.world.name == "world") {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.y < -16.0 && player.world.name == "game" && player in game_players) {
            player.health = 0.0
        }
    }
}