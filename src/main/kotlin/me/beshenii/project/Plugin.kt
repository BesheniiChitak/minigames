package me.beshenii.project

import me.beshenii.project.StatsLoad.load
import me.beshenii.project.StatsLoad.save
import me.beshenii.project.command.HostCommand
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import net.minecraft.world.BossEvent.BossBarColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: Plugin

val games = listOf("Столбы", "Столбы_2")

val bossbar = BossBar.bossBar(text(""), 1f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)

class Plugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this

        load()

        Bukkit.getPluginManager().registerEvents(GlobalListener, this)

        plugin.getCommand("host")!!.setExecutor(HostCommand)

        server.commandMap.getCommand("plugins")?.permission = "*"
    }

    override fun onDisable() {
        save()
    }
}
