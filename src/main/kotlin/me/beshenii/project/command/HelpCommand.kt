package me.beshenii.project.command

import me.beshenii.project.util.other.color
import me.beshenii.project.util.other.light_blue
import net.kyori.adventure.text.Component.text
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object HelpCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) return false
        sender.sendMessage("")
        sender.sendMessage(text(" Помощь по настройкам миниигр. (/setting <настройка> <значение>)").color(light_blue))
        sender.sendMessage("")
        sender.sendMessage(text("  - pillarsEqual [По умолчанию true] » В столбах все получают одинаковые предметы.").color(light_blue))
        sender.sendMessage(text("  - pillarsTimer [По умолчанию 15] » Отсчёт до следующего предмета в столбах.").color(light_blue))
        sender.sendMessage("")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }
}

