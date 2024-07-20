package me.beshenii.project.util.other

import me.beshenii.project.bossbar
import me.beshenii.project.util.cur_status
import me.beshenii.project.util.queue_join
import me.beshenii.project.util.spectate
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun intArrayOf(range: IntRange) =
    IntArray(range.last - range.first + 1) { range.first + it }

fun Map<*, *>.mapToString(): String {
    var result = ""
    val first = this.keys.first()
    this.forEach { (key, value) ->
        var add = "$key — $value"
        if (key == first) add = " | $add"
        result += add
    }
    return result
}

fun String.stringToMap(): MutableMap<String, Int> {
    val result = mutableMapOf<String, Int>()
    val arrays = this.split(" | ")
    arrays.forEach {
        val array = it.split(" — ")
        result[array[0]] = array[1].toInt()
    }
    return result
}

fun Player.reset() {
    this.inventory.clear()
    this.teleport(Bukkit.getWorld("world")!!.spawnLocation)
    this.gameMode = GameMode.ADVENTURE

    this.hideBossBar(bossbar)

    when (cur_status) {
        "queue" -> this.inventory.setItem(4, queue_join)
        "running" -> this.inventory.setItem(4, spectate)
    }
}
