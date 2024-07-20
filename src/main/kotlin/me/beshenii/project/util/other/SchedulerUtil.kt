package me.beshenii.project.util.other

import me.beshenii.project.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer
import kotlin.time.Duration

fun runTaskTimer(period: Duration, delay: Int = 0, action: (BukkitTask) -> Unit) {
    Bukkit.getScheduler().runTaskTimer(plugin, Consumer { action(it) }, delay.toLong(), period.inWholeTicks)
}

val Duration.inWholeTicks get() = inWholeMilliseconds / 50

fun runTaskLater(delay: Int, action: BukkitTask.() -> Unit) {
    Bukkit.getScheduler().runTaskLater(plugin, Consumer { it.action() }, delay.toLong())
}

fun runTaskAsync(action: BukkitTask.() -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, action)
}
