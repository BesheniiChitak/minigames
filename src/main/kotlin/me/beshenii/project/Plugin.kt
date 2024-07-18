package me.beshenii.project

import me.beshenii.project.StatsLoad.load
import me.beshenii.project.StatsLoad.save
import me.beshenii.project.command.ExampleCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: Plugin

class Plugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this

        load()

        Bukkit.getPluginManager().registerEvents(GlobalListener, this)

        plugin.getCommand("")!!.setExecutor(ExampleCommand)
    }

    override fun onDisable() {
        save()
    }
}
