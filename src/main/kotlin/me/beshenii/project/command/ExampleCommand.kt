package me.beshenii.project.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object ExampleCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size != 1) return false
        sender.sendMessage("1")
        return true
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>?
    ): MutableList<String> {
        return mutableListOf()
    }
}

