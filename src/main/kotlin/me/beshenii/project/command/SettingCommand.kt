package me.beshenii.project.command

import me.beshenii.project.games
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object SettingCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size != 2) return false
        sender.sendMessage("1")
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
        when (size) {
            1 -> {
                list.addAll(games.filter { it.contains(lastArg) })
            }
            2 -> {
                list.add("Макс. Игроков")
            }
            3 -> {
                list.add("Мин. Игроков")
            }
        }
        return list.toMutableList()
    }
}


