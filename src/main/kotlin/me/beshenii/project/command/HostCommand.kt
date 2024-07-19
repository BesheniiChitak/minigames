package me.beshenii.project.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object HostCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size < 1) return false
        val game = args[0].lowercase()
        when (game) {
            "столбы" -> {

            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val list = mutableListOf<String>()
        val lastArg = args.last()
        val size = args.size
        if (size == 1) {
            list.add("Столбы")
        }
        return list
    }
}

