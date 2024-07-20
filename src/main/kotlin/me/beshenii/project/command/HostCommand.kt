package me.beshenii.project.command

import me.beshenii.project.games
import me.beshenii.project.util.cur_game
import me.beshenii.project.util.hostQueue
import me.beshenii.project.util.max_players
import me.beshenii.project.util.min_players
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object HostCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size < 1) return false
        if (cur_game != null) {
            sender.sendMessage("Игра уже запущена.")
            return false
        }
        val game = args[0]
        if (game in games) {
            sender.sendMessage("Успешный хост.")
            cur_game = game
            max_players = args.getOrNull(1)?.toIntOrNull() ?: 16
            min_players = args.getOrNull(2)?.toIntOrNull() ?: 2
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

