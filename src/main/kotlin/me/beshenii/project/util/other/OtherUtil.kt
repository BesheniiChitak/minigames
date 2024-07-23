package me.beshenii.project.util.other

import me.beshenii.project.bossbar
import me.beshenii.project.util.cur_status
import me.beshenii.project.util.queue_join
import me.beshenii.project.util.spectate
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun Player.reset() {
    this.inventory.clear()
    this.teleport(Bukkit.getWorld("world")!!.spawnLocation)
    this.gameMode = GameMode.ADVENTURE

    this.isInvulnerable = true

    this.exp = 0f
    this.level = 0

    this.health = 20.0
    this.clearActivePotionEffects()

    this.enderChest.clear()

    this.hideBossBar(bossbar)

    when (cur_status) {
        "queue" -> this.inventory.setItem(4, queue_join)
        "running" -> this.inventory.setItem(4, spectate)
    }
}

fun <K, V> MutableMap<K, V>.clone(): MutableMap<K, V> {
    val map = LinkedHashMap<K, V>()
    for ((k, v) in this) map[k] = v
    return map
}
