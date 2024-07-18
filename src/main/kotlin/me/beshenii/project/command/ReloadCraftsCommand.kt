package me.beshenii.project.command

import me.beshenii.project.util.allItems
import me.beshenii.project.util.doneRecipes
import me.beshenii.project.util.reloadCrafts
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object ReloadCraftsCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) return false
        doneRecipes.clear()
        reloadCrafts()
        return true
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>?
    ): MutableList<String> {
        return mutableListOf("")
    }
}

