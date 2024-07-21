package me.beshenii.project

import me.beshenii.project.StatsLoad.load
import me.beshenii.project.StatsLoad.save
import me.beshenii.project.command.HelpCommand
import me.beshenii.project.command.HostCommand
import me.beshenii.project.command.SettingCommand
import me.beshenii.project.util.gameEnd
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: Plugin

val games = listOf("Столбы")

val bossbar = BossBar.bossBar(text(""), 1f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10)

class Plugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this

        load()

        Bukkit.getPluginManager().registerEvents(GlobalListener, this)

        plugin.getCommand("host")!!.setExecutor(HostCommand)
        plugin.getCommand("setting")!!.setExecutor(SettingCommand)
        plugin.getCommand("f1nnyhelp")!!.setExecutor(HelpCommand)

        server.commandMap.getCommand("plugins")?.permission = "*"

        gameEnd()
    }

    override fun onDisable() {
        save()
    }
}
