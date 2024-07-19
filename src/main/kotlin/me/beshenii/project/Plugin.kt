package me.beshenii.project

import me.beshenii.project.StatsLoad.load
import me.beshenii.project.StatsLoad.save
import me.beshenii.project.command.ExampleCommand
import me.beshenii.project.command.HostCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: Plugin

class Plugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this

        load()

        Bukkit.getPluginManager().registerEvents(GlobalListener, this)

        plugin.getCommand("host")!!.setExecutor(HostCommand)
    }

    override fun onDisable() {
        save()
    }
}
