@file:OptIn(ExperimentalContracts::class)

package me.beshenii.project.command

import me.beshenii.project.util.*
import me.beshenii.project.util.other.color
import me.beshenii.project.util.other.pluginMessage
import me.beshenii.project.util.other.plus
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import kotlin.contracts.ExperimentalContracts

object GetItemCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size !in listOf(1, 2, 3) || sender !is Player) return false
        val item = getItem(args[0])
        val playerNames = Bukkit.getOnlinePlayers().map { it.name }
        val player: Player = if (args.size in listOf(1,2)) sender
        else if (args[2] in playerNames) Bukkit.getPlayer(args[2])!! else sender
        val amount = args.getOrNull(1)?.toIntOrNull() ?: 1
        item.amount = amount
        if (!item.isNullOrAir()) player.inventory.addItem(item)
        Bukkit.getOnlinePlayers().forEach {
            if (it.isOp) {
                it.sendMessage(
                    pluginMessage +
                    translatable(
                        "commands.give.success.single",
                        text(amount),
                        item.displayName(),
                        text(player.name)
                    ).color(0xffffff)
                )
            }
        }
        return true
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>
    ): MutableList<String> {
        val arg = p3.last()
        val list = mutableListOf<String>()
        when (p3.size) {
            1 -> list.addAll(allItems.keys)
            2 -> list.add("<int>")
            3 -> list.addAll(Bukkit.getOnlinePlayers().map { it.name })
        }
        return list.filter { it.contains(arg.lowercase()) }.toMutableList()
    }
}

