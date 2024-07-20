package me.beshenii.project.command

import me.beshenii.project.cur_game
import me.beshenii.project.games
import me.beshenii.project.util.hostQueue
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object HostCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size < 1) return false
        val game = args[0]
        if (game in games) {
            cur_game = game
            hostQueue()
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
            list.addAll(games)
        }
        return list.filter { it.contains(lastArg) }.toMutableList()
    }
}

